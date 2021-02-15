# 캔들 지표

## 1. 지표

CCI, MA, Bollinger band 등의 주가 보조지표.

## 2. 지표 구현

지표(index)는 캔들차트에 의존적이다. 따라서 라이프 사이클이 CandleList class에 종속되어 있으므로, 지표 클래스를 단독으로 사용하는 것은 불가.

CandleList 클래스 내에 지표를 계산하기 위해서, addIndex 등의 메소드가 존재. 해당 메소드는 Class<? extends Indexable>타입을 매개변수로 받는다. Indexable을 구현하는 클래스는 지표를 계산하는 기능을 수행할 수 있다. 하지만 지표를 제공하는 기능(getter)과 CandleList에 신규 candle이 추가되었을 때 내부적으로, pop&calculate&push 기능을 수행할 수 없으므로, 필히 BasicIndexAbstract 추상 클래스를 상속받아야 한다.

## 3. Indexable interface

CandleList에서 새로운 candle이 추가되어 CandleList::registerNewCandle, 또는 CandleList::updateCurrentCandle이 호출될 때 내부적으로 지표를 업데이트 하거나 새로 등록할 수 있어야한다. Indexable 인터페이스는 이에 대한 책임.

## 4. BasicIndexAbstrace

지표 클래스라면 필히 수행할 수 있어야 하는 책임을 정의해 놓았다. Indexalbe::calculateNewCandle, Indexalbe::calculateCurrentCandle 메소드를 보편적인 상황에 맞게(calculateNewCandle이 호출될 때에는 보통 기존의 indexObject를 pop하고, 새롭게 계산된 indexObject를 push 해야 한다.) 오버라이딩 해놓았으니, 자식클래스에서 다시 오버라이딩 하는 것은 비추.

## 5. IndexClass

BollingerBand.java, MovingAverage.java 등의 클래스가 이에 해당한다. 정확히는 BasicIndexAbstrace 클래스를 상속하는 클래스가 IndexClass 라고 할 수 있다. calculate 메소드를 재정의 해야 한다.

## 6. IndexObject

IndexClass의 calculate 결과의 타입 클래스가 이에 해당한다. 예를 들어, BollingerBand 클래스에 해당하는 IndexObject는 BBObject 클래스이다.
지표의 계산결과는 IndexObject를 통해서 접근할 수 있다.

## 7. 구현 예

```JAVA
public class BollingerBand extends BasicIndexAbstract<BBObject> {

    public BollingerBand(CoinEnum coinType, IntervalEnum interval, CandleList reference) {
        super(coinType, interval, reference);
    }

    @Override
    protected BBObject calculate(Candle item) {
        return new BBObject(calUpperBB(item, 20), calMiddleBB(item, 20), calLowerBB(item, 20));
    }

    private double calUpperBB(Candle item, int period) {...}
    private double calMiddleBB(Candle item, int period) {...}
    private double calLowerBB(Candle item, int period) {...}
}

public class BBObject {
    private double upperBB;
    private double middleBB;
    private double lowerBB;

    public BBObject(all field args)
}
```
