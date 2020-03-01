package bikcrum.elevationview;

public class Segment {
    public float elevationLow;
    public float elevationHigh;
    public float width;

    public Segment(float elevationLow, float elevationHigh, float width) {
        this.elevationLow = elevationLow;
        this.elevationHigh = elevationHigh;
        this.width = width;
    }
}
