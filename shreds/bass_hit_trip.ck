.5::second => dur T;
T - (now % T) => now;

SndBuf strong => Gain b => dac;
me.dir() + "data/strongbad_bass.wav" => strong.read;
0.3 => b.gain;

0 => strong.pos;
0.75::second => now;
0 => strong.pos;
0.75::second => now;
0 => strong.pos;
0.50::second => now;
0 => strong.pos;
0.75::second => now;
0 => strong.pos;
0.75::second => now;



