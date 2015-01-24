// noise generator, biquad filter, dac (audio output) 
Noise n => BiQuad f => dac;
// set biquad pole radius
.99 => f.prad;
// set biquad gain
.05 => f.gain;
// set equal zeros 
1 => f.eqzs;
// our float
0.0 => float t;

// infinite time-loop
// sweep the filter resonant frequency
220.000 => f.pfreq;
// advance time
1::second => now;
