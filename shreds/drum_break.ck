.5::second => dur T;
T - (now % T) => now;

SndBuf bass => Gain b => dac;
SndBuf hihat => Gain h => dac;
SndBuf snare => Gain s => dac;


me.dir() + "data/kick.wav" => bass.read;
me.dir() + "data/hihat.wav" => hihat.read;
me.dir() + "data/snare.wav" => snare.read;

while(true){
    0 => bass.pos;
    0.5::second => now;
    0 => snare.pos;
    0.75::second => now;

    0 => bass.pos;
    0.25::second => now;
    0 => hihat.pos;
    0.5::second => now;
    0 => snare.pos;
    0.75::second => now;
}
