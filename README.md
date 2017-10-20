# IntervalRangeBar

  A simple, double- thumb bar that allows you to pick an interval from given data set.
  Current thumb value is updated to nearest discrete value as it moves.

  ![Lol](http://gph.is/2ywMPA4)

  Add the dependency to you build.gradle:

  ```java
  compile 'com.github.JanPawlov:IntervalRangeBar:1.0'
  ```

  To implement IntervalRangeBar, simply add this to your layout:

  ```xml
  <pl.applover.intervalrangebar.IntervalRangeBar
    android:id="@+id/time_interval_range_bar"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:barHeight="8dp"
    app:interval="1"
    app:maxValue="24"
    app:minValue="0"
    app:leftStartValue="5"
    app:rightStartValue="18"
    app:thumbSize="24dp" />
  ```

Or set start values dynamically:

```java
rangeBar.setStartValues(_leftStartValue_,_rightStartValue_)
```

Attach listener to get current thumb values:

```java
rangeBar.attachValueListener(object : IntervalRangeBar.ThumbsValueListener {
    override fun thumbsValuesUpdated(leftThumbValue: Int, rightThumbValue: Int) {
      //TODO Do what you want with the values
    }
})
```
