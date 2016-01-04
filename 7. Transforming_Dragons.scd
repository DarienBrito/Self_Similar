/*
- An implementation in SuperCollider of Tom Johnson's techniques, as described in the "Self-Similar Melodies" book -
by Darien Brito (2015)
http://www.darienbrito.com

This work is licensed under Creative Commons
Attribution-NonCommercial-ShareAlike 4.0 International Licence (CC BY-NC-SA 4.0)

To view a copy of this license go to:
http://creativecommons.org/licenses/by-nc-sa/4.0/
*/

/*______________________________________________________________

                     Transforming Dragons
________________________________________________________________*/


/*_______________________

Constants
_________________________*/

//Test sound:
(
SynthDef(\sine, {|freq = 440, amp = 0.1, rel = 0.8|
	var sig = SinOsc.ar(freq, 0, amp);
	var env = EnvGen.kr(Env.perc(releaseTime: rel),doneAction:2);
	sig = sig*env;
	Out.ar(0, sig!2)
}).add;

// Parameters
~scale = Scale.minor;
~amp = Pfunc({ |ev| if(ev.degree < 1) { 0.1 } { ev.degree.reciprocal * 0.25}} );
)

// Before continuing, notice that I've created 2 new instance methods for SequenceableCollection:
.binarize // Will keep 0's and make a 1 for every value > 1
.binarizeInvert // Will binarize and invert the vals. if 0 then 1 else 0
// This are part of the git repository
[1,2,3,4,0].binarize;
[0,1,0].binarizeInvert;

// Considering the dragon...
// After n folds, there are 2^n parts and 2^n - 1 creases
(
var folds =  10;
var parts = 2.pow(folds);
var creases = parts - 1;
"Parts: %".format(parts).postln;
"Creases: %".format(creases).postln;
)

// The number of folds outnumbers the number of creases by 1
// At every level, the type 1 creases outnumber the type 0 creases by 1 due to the middle fold

// For convenience, we put our dragon generator in a function:
(
~dragonMaker = { |n|
	var center;
	var col = List[1];
	var invert = List(), final = List(), seq = List(), temp;
	var tail;
	seq.add(col);
	n.do{
		tail = col.size;
		invert = col.binarizeInvert.reverse; // binarizeInvert simply does this: if(item != 0) {0} {1}}
		temp = col.insert(tail,1); // This is desctructive so... a workaround?
		final = temp ++ invert;
		col = final;
		seq.add(col);
	};
	seq.do{|item| item.removeAt( item.size -1 )}; //Remove the last item of sequence
	seq = seq.flatten;
})

~dragonMaker.(4)

      1
     110
   1101100
110110011100100

(
var limit = 10;
var dur = 0.15;
var interval = 2;
var seq =
~sequence = ~dragonMaker.(limit);
p = Pbindef(\dragon1,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)

      1+
     1+1+0-
   1+1+0-1+1+0-0-
1+1+0-1+1+0-0-1+1+1+0-0-1+0-0-
// A dragon which goes up one degree when 1 and one degree below when 0
(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = ~dragonMaker.(limit);
var val = 0;

p = Pbindef(\dragon2,
	\instrument, \sine,
	\scale, ~scale,
	\index, Pseries(0),
	\degree, Pfunc({|ev|
		var i = ev.index;
		if (seq[i] > 0) { val = val + 1 } { val = val - 1};
		val * interval;
	}),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)
