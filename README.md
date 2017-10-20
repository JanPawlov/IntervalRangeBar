# IntervalRangeBar

  A simple, double- thumb bar that allows you to pick an interval from given data set.
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
