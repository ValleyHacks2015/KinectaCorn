.5::second => dur T;
T - (now % T) => now;


SinOsc s => dac;

130.813 => s.freq;

while(true){
2::second => now;
0 => s.gain;
2::second => now;
1 => s.gain;
}


