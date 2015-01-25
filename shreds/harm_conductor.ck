OscSend xmit;
xmit.setHost("localhost", 1234);

string m;

if( me.args() ){ me.arg(0) => m;}

xmit.startMsg("conductor/downbeat, s");
xmit.addString(m);
