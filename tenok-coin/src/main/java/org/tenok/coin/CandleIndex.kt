package org.tenok.coin

import java.util.ArrayList
import java.util.Date
import java.util.Scanner
import java.util.concurrent.CompletableFuture
import java.util.stream.Stream

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.tenok.coin.data.BybitRestDAO
import org.tenok.coin.data.entity.impl.Candle
import org.tenok.coin.data.entity.impl.CandleList
import org.tenok.coin.type.CoinEnum
import org.tenok.coin.type.IntervalEnum

import java.io.File
import java.io.IOException
import java.net.http.HttpResponse

object CandleIndex {
    internal var restDAO = BybitRestDAO()
    internal val ROOT_PATH = "./candle_cached"

    @Throws(IOException::class)
    @JvmStatic
    fun main(args: Array<String>) {

        println("candleCache!\n1. cache All")
        var i = 2
        for (coinType in CoinEnum.values()) {
            System.out.printf("%d. cache %s", i++, coinType.name)
        }

        val scan = Scanner(System.`in`)
        var selectedCoin: CoinEnum? = null
        val selected = scan.nextInt()
        if (selected == 1) {
            for (coinType in CoinEnum.values()) {
                for (interval in IntervalEnum.values()) {
                    cacheKLine(coinType, interval)
                }
            }
        } else {
            selectedCoin = CoinEnum.values()[2 + selected]
            for (interval in IntervalEnum.values()) {
                cacheKLine(selectedCoin, interval)
            }
        }
        scan.close()

    }

    @Throws(IOException::class)
    fun cacheKLine(coinType: CoinEnum, interval: IntervalEnum) {
        System.out.printf("start cache %s %s%n", coinType.getLiteral(), interval.apiString)
        File(ROOT_PATH + "/" + coinType.getLiteral() + "/").mkdirs()
        File(ROOT_PATH + "/" + coinType.getLiteral() + "/" + interval.apiString + ".json").createNewFile()

        // Bybit 서버로 부터 전체 캔들 받아옴.
        val tempCandleList = requestAllCandleList(coinType, interval)
        System.out.printf("size of Loaded list: %d%n", tempCandleList.size)

        // 중복된 캔들 리스트 삭제
        removeDuplicateCandle(tempCandleList)
        System.out.printf("removed duplicate candles. size is %d%n", tempCandleList.size)

        // CandleList 객체 생성하여, register한다.
        val candleList = CandleList(coinType, interval)
        tempCandleList.stream().forEachOrdered { candle -> candleList.registerNewCandle(candle) }

        candleList.sort { candle1, candle2 -> candle1.startAt.compareTo(candle2.startAt) }
        System.out.printf("Final size of candle list: %d%n", candleList.size)

        val mapper = ObjectMapper()

        mapper.writeValue(File(ROOT_PATH + "/" + coinType.getLiteral() + "/" + interval.apiString + ".json"),
                candleList)

        System.out.printf("Successfully cached candle List. size is %dMB%n",
                File(ROOT_PATH + "/" + coinType.getLiteral() + "/" + interval.apiString + ".json").length()
                        / 1024L / 1024L)
    }

    /**
     * Bybit API를 통해 현재부터, 과거까지 전체의 캔들 리스트를 요청하여 받아온다.
     *
     * @param coinType CoinEnum
     * @param interval IntervalEnum
     * @return Whole Candle List
     */
    fun requestAllCandleList(coinType: CoinEnum, interval: IntervalEnum): MutableList<Candle> {
        val pivotCandle = findPivotCandle(coinType, interval)
        val pivotTime = pivotCandle.startAt.time / 1000L
        val responseList = ArrayList<JSONArray>()

        var requestIter = 1
        var previousId: Long = 0
        var currentId: Long = 0
        while (true) {
            var responseJson: JSONObject? = null
            responseJson = restDAO.requestKline(coinType, interval, 200,
                    Date(pivotTime * 1000L - 200L * interval.sec * 1000L * requestIter.toLong()))

            val currentResponse = (responseJson!!["result"] as JSONArray)[0] as JSONObject
            responseList.add(responseJson["result"] as JSONArray)

            currentId = currentResponse["id"] as Long
            if (previousId == currentId) {
                break
            } else if (requestIter % 10 == 0) {
                System.out.printf(String.format("\rcandle list loading epoch %d", requestIter))
            }
            previousId = currentId
            requestIter++
        }
        println("loading complete")
        val tempCandleList = ArrayList<Candle>()

        responseList.stream().flatMap { inner -> inner.stream() as Stream<JSONObject> }.forEach { kLineObject ->
            val open = (kLineObject["open"] as Number).toDouble()
            val high = (kLineObject["high"] as Number).toDouble()
            val low = (kLineObject["low"] as Number).toDouble()
            val close = (kLineObject["close"] as Number).toDouble()
            val volume = (kLineObject["volume"] as Number).toDouble()
            val startAt = Date(kLineObject["start_at"] as Long * 1000L)
            tempCandleList.add(Candle(startAt, volume, open, high, low, close))
        }
        tempCandleList.add(pivotCandle)

        println("parse success")
        tempCandleList.sort { candle1, candle2 -> candle1.startAt.compareTo(candle2.startAt) }

        return tempCandleList
    }

    /**
     * cache 과정에서 중복된 캔들 리스트를 삭제.
     *
     * @param candleList 캔들 리스트
     */
    fun removeDuplicateCandle(candleList: MutableList<Candle>) {
        candleList.sort { candle1, candle2 -> -1 * candle1.startAt.compareTo(candle2.startAt) }

        val duplicateIndexList = ArrayList<Int>()

        var indexCandle = candleList[0]
        for (j in 1 until candleList.size) {
            if (candleList[j].startAt.time == indexCandle.startAt.time) {
                duplicateIndexList.add(j)
            } else {
                indexCandle = candleList[j]
            }
        }

        for (j in duplicateIndexList.size - 1 downTo -1 + 1) {
            candleList.removeAt(duplicateIndexList[j])
        }

        candleList.sort { candle1, candle2 -> candle1.startAt.compareTo(candle2.startAt) }
    }

    @Throws(JsonParseException::class, JsonMappingException::class, IOException::class)
    fun updateCandle(coinType: CoinEnum, interval: IntervalEnum) {

        val list = ObjectMapper().readValue(File("./candle_cached/bitcoin/1.json"), CandleList::class.java)
        val latestCandle = list[list.size - 1]

        val latestDate = latestCandle.startAt

        val pivotCandle = findPivotCandle(coinType, interval)
        var i = 0
        var outFlag = true

        val responseList = ArrayList<JSONObject>()

        while (outFlag) {
            val response = restDAO.requestKline(coinType, interval, 200,
                    Date(pivotCandle.startAt.time - (200000L * i.toLong() * interval.sec + interval.sec * 1000L)))
            val responseArray = response["result"] as JSONArray
            responseList.add(response)

            for (`object` in responseArray) {
                val kLine = `object` as JSONObject
                if (kLine["start_at"] as Long == latestDate.time / 1000L) {
                    println("hi")
                    outFlag = false
                    break
                }
            }
            println()
            i++
        }

    }

    fun findPivotTime(response: JSONObject): Long {
        return ((response["result"] as JSONArray)[0] as JSONObject)["start_at"] as Long
    }

    fun findPivotCandle(coinType: CoinEnum, interval: IntervalEnum): Candle {
        val response = restDAO.requestKline(coinType, interval, 1,
                Date(System.currentTimeMillis() - interval.sec * 1000L))
        val resArr = response["result"] as JSONArray
        val kLineObject = resArr[0] as JSONObject
        val open = (kLineObject["open"] as Number).toDouble()
        val high = (kLineObject["high"] as Number).toDouble()
        val low = (kLineObject["low"] as Number).toDouble()
        val close = (kLineObject["close"] as Number).toDouble()
        val volume = (kLineObject["volume"] as Number).toDouble()
        val startAt = Date(kLineObject["start_at"] as Long * 1000L)
        return Candle(startAt, volume, open, high, low, close)
    }

    fun requestCandle(coinType: CoinEnum, interval: IntervalEnum, startAt: Date): Candle {
        val response = restDAO.requestKline(coinType, interval, 1, startAt)
        val resArr = response["result"] as JSONArray
        val kLineObject = resArr[0] as JSONObject
        val open = (kLineObject["open"] as Number).toDouble()
        val high = (kLineObject["high"] as Number).toDouble()
        val low = (kLineObject["low"] as Number).toDouble()
        val close = (kLineObject["close"] as Number).toDouble()
        val volume = (kLineObject["volume"] as Number).toDouble()
        val start = Date(kLineObject["start_at"] as Long * 1000L)
        return Candle(start, volume, open, high, low, close)
    }

    fun requestCandleParallel(coinType: CoinEnum,
                              interval: IntervalEnum, startAt: Date): CompletableFuture<HttpResponse<String>> {
        return restDAO.requestKlineHttp2(coinType, interval, 1, startAt)
    }
}
