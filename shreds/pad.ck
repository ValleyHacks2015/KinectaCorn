.5::second => dur T;
T - (now % T) => now;


SinOsc s => Gain p => dac;

0.15 => p.gain;

float o1;
float o2;

130.813 => o1;
123.471 => o2;

while(true){
o1 => s.freq;
6::second => now;
o2 => s.freq;
2::second => now;
}

