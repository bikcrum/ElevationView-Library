# ElevationView-Library

Present 2D graph of your elevation data. 

Initial the data:
```
<resources>
    <string-array name="distanceX">
        <item>0</item>
        <item>50</item>
        <item>100</item>
        <item>150</item>
        <item>200</item>
        <item>250</item>
        <item>300</item>
        <item>350</item>
        <item>400</item>
    </string-array>

    <string-array name="elevationY">
        <item>120</item>
        <item>125</item>
        <item>200</item>
        <item>190</item>
        <item>370</item>
        <item>370</item>
        <item>426</item>
        <item>551</item>
    </string-array>

</resources>
```

Add elevation view
```
<bikcrum.elevationview.ElevationView
        android:id="@+id/elevation_view"
        android:layout_width="match_parent"
        android:layout_height="260dp"
        app:distance_x="@array/distanceX"
        app:elevation_y="@array/elevationY"
        app:graphPaddingBottom="40dp"
        app:graphPaddingEnd="48dp"
        app:graphPaddingStart="40dp"
        app:graphPaddingTop="60dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:copyright="Test"
        app:showStats="true" />
```

Here is how your elevation view looks. [LINK](https://i.ibb.co/2c7nSrm/Screenshot-1583051674.png):
<a href="https://ibb.co/XFZ2DPN"><img src="https://i.ibb.co/2c7nSrm/Screenshot-1583051674.png" alt="Screenshot-1583051674" border="0"></a>
