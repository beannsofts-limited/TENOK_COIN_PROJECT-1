# 1. Connection Provider

Java application(Coin Tenok)과 Websocket을 통해서 서버 역할을 수행. 프로그램에 주문 명령 등을 내릴 수 있으며,
실시간 동작 상황을 감시할 수 있게 한다.

## 2. websocket

### 2.1. URI

    wss://localhost/tenok

### 2.2. payload

서버로 요청할 때 사용되는 payload는 다음 기준을 따른다.

``` JSON
{
    "request": REQUEST_ENUM,
    "params": [
        "param1": "someting"
        "param2": "someting",
        ...
    ]
}
```

서버의 응답 payload는 다음 기준을 따른다.

``` JSON
{
    "request": REQUEST_ENUM,
    "params": [
        "param1": "someting"
        "param2": "someting",
        ...
    ],
    "result": {
        서버의 응답 데이터
    },
    "isSuccess": true   // 요청 처리 성공 여부
}
```

### 2.3. 기능

#### 2.3.1. 캔들차트 제공

    wss://localhost/tenok/candle

## Request

|param|값|설명|필수여부|
|----|----|----|----|
|symbol|CoinEnum|요청할 캔들의 코인 타입|true|
|interval|IntervalEnum|요청할 캔들의 봉 간격|true|
|realtime|boolean|실시간 데이터 제공 여부|true|

## Response

만약 <code>realtime = false</code> 라면 다음과 같은 response를 보낸다.
``` JSON
{
    "result": {
        "data": [
            {
                "startAt": 1613295435579,    // current time in milliseconds
                "volume": 135.5, // double
                "open": 4532.5,  // double
                "high": 4618.5,  // double
                "low": 4518.0,   // double
                "close": 4612.5, // double
                "ma5": 4525.0,   // double
                "ma10": 4525.0,  // double
                "ma20": 4518.0,  // double
                "ma60": 4507.5,  // double
                "ma120": 4398.5, // double
                "upperBB": 5132.5,   // double
                "middleBB": 4525.5,  // double
                "lowerBB": 4178.0,   // double
            },
            {
                ...
            }
        ]
    }
}
```

