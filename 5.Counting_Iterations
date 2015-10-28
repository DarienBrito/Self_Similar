/*
- An implementation in SuperCollider of Tom Johnson's techniques, as described in the "Self-Similar Melodies" book -
by Darien Brito (2015)
http://www.darienbrito.com

This work is licensed under Creative Commons
Attribution-NonCommercial-ShareAlike 4.0 International Licence (CC BY-NC-SA 4.0)

To view a copy of this license go to:
http://creativecommons.org/licenses/by-nc-sa/4.0/
*/

/*_______________________________________________________________________

                     Counting with computer (iterations)
_________________________________________________________________________*/


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

/*__________________________________________

Simple additive counting (double iteration)
____________________________________________*/

1
12
123
(...)

(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
// Make sequence
limit.do{|i| i.do{|j| seq.add(j)}; seq.add(i)};
~sequence = List.new;

p = Pbindef(\iter1,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)

/*________________________

Triple iteration
__________________________*/

//////////
// Note
//////////

// This would be the for loop style of doing this:
(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
// Make sequence
for(1,limit,{|i|
	for(1,i,{|j|
		for(1,j,{|k|
			seq.add(k)
		});
	});
});
seq
)

1
1 12
1 12 123
1 12 123 1234
(...)

// I will use however a more compact (but perhaps less clear) SC style:
(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
// Make sequence
limit.do{|i| (i+1).do{|j| (j+1).do{|k|  seq.add(k) }}}; //Ask for k
~sequence = List.new;

p = Pbindef(\iter,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)
~sequence

1
1 22
1 22 333
1 22 333 444
(...)

(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
// Make sequence
limit.do{|i| (i+1).do{|j| (j+1).do{|k|  seq.add(j) }}}; //<----Ask for j
~sequence = List.new;

p = Pbindef(\iter,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)

1
222
333333
4444444444
(...)

(
var limit = 10;
var dur = 0.15;
var interval = 2;
var seq = List();
// Make sequence
limit.do{|i| (i+1).do{|j| (j+1).do{|k|  seq.add(i) }}}; //<---Ask for i

~sequence = List.new;
p = Pbindef(\iter,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)

11
21 22 22
31 32 32 33 33 33
41 42 42 43 43 43 44 44 44 44
(...)

(
var limit = 10;
var dur = 0.15;
var interval = 2;
var seq = List();
// Make sequence
limit.do{|i| (i+1).do{|j| (j+1).do{|k|  seq.add(i); seq.add(j)}}}; //<--- Ask for i, j at own level
~sequence = List.new;

p = Pbindef(\iter,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)

11
21 21 22
31 31 32 31 32 33
41 41 42 41 42 43 41 42 43 44
(...)

(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
// Make sequence
limit.do{|i| (i+1).do{|j| (j+1).do{|k|  seq.add(i); seq.add(k)}}}; //<--- Ask for i, k at own level
~sequence = List.new;

p = Pbindef(\iter,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)

11
12 22 22
13 23 23 33 33 33
14 24 24 34 34 34 44 44 44 44
(...)

(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
// Make sequence
limit.do{|i| (i+1).do{|j| (j+1).do{|k|  seq.add(j); seq.add(i)}}}; //<--- Ask for j, i at own level
~sequence = List.new;

p = Pbindef(\iter,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)

11
11 21 22
11 21 22 31 32 33
11 21 22 31 32 33 41 42 43 44
(...)

(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
// Make sequence
limit.do{|i| (i+1).do{|j| (j+1).do{|k|  seq.add(j); seq.add(k)}}}; //<--- Ask for j, k at own level
~sequence = List.new;

p = Pbindef(\iter,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)

11
12 12 22
13 13 23 13 23 33
14 14 24 14 24 34 14 24 34 44
(...)

(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
// Make sequence
limit.do{|i| (i+1).do{|j| (j+1).do{|k|  seq.add(k); seq.add(i)}}}; //<--- Ask for k, i at own level
~sequence = List.new;

p = Pbindef(\iter,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)

11
11 12 22
11 12 22 13 23 33
11 12 22 13 23 33 14 24 34 44
(...)

(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
// Make sequence
limit.do{|i| (i+1).do{|j| (j+1).do{|k|  seq.add(k); seq.add(j)}}}; //<--- Ask for k, j at own level
~sequence = List.new;

p = Pbindef(\iter,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)

111
211 221 222
311 321 322 331 332 333
411 421 422 431 432 433 441 442 443 444
(...)

(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
// Make sequence
limit.do{|i| (i+1).do{|j| (j+1).do{|k| seq.add(i); seq.add(j); seq.add(k)}}}; //<--- Ask for i, j, k at own level
~sequence = List.new;

p = Pbindef(\iter,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)

111
121 122 222
131 132 232 133 233 333
141 142 242 143 243 343 144 244 344 444
(...)

(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
// Make sequence
limit.do{|i| (i+1).do{|j| (j+1).do{|k| seq.add(j); seq.add(i); seq.add(k)}}}; //<--- Ask for j, i, k at own level
~sequence = List.new;

p = Pbindef(\iter,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)

111
112 122 222
113 123 223 133 233 333
(...)

(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
// Make sequence
limit.do{|i| (i+1).do{|j| (j+1).do{|k| seq.add(k); seq.add(j); seq.add(i)}}}; //<--- Ask for k, j, i at own level
~sequence = List.new;

p = Pbindef(\iter,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)

~sequence

/*__________________________________

 Hacking the SC implementation
____________________________________*/

// See what happens with this:
3.do{|i| ("i is: " ++ i).postln; i.do{|j| ("j is: "++j).postln}}
// That's something we can use...

(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
//Ask for i, j without compensation
//What happens here is that the first time only i get's evaluated sine 0.do{} ignores the evaluation
//If not clear run this again the function below "Hacking the SC implementation"
limit.do{|i| i.do{|j| j.do{|k|  seq.add(i); seq.add(j) }}};
~sequence = List.new;

p = Pbindef(\iter,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)


(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
limit.do{|i| i.do{|j| j.do{|k|  seq.add(i); seq.add(k) }}};
~sequence = List.new;

p = Pbindef(\counting,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)


(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
limit.do{|i| i.do{|j| j.do{|k|  seq.add(j); seq.add(k) }}};
~sequence = List.new;

p = Pbindef(\counting,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)

(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
limit.do{|i| i.do{|j| j.do{|k|  seq.add(i); seq.add(j); seq.add(k) }}};
~sequence = List.new;

p = Pbindef(\counting,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)

// etc...

/*__________________________________

 Performing operations
____________________________________*/

2
3 4 4
4 5 5 6 6 6
5 6 6 7 7 7 8 8 8 8
(...)


(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
// Make sequence
limit.do{|i|  (i+1).do{|j| (j+1).do{|k|  seq.add((i+1)+(j+1) ) }}}; //<----Ask for i + j
~sequence = List.new;

p = Pbindef(\iterOp,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)


2
3 3 4
4 4 5 4 5 6
5 5 6 5 6 7 5 6 7 8
6 6 7 6 7 8 6 7 8 9 6 7 8 9 10
(...)

(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
// Make sequence
limit.do{|i|  (i+1).do{|j| (j+1).do{|k|  seq.add((i+1)+(k+1) ) }}}; //<----Ask for i + k
~sequence = List.new;

p = Pbindef(\iterOp,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)

2
2 3 4
2 3 4 4 5 6
2 3 4 4 5 6 5 6 7 8
2 3 4 4 5 6 5 6 7 8 6 7 8 9 10
(...)

(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
// Make sequence
limit.do{|i|  (i+1).do{|j| (j+1).do{|k|  seq.add((j+1)+(k+1) ) }}}; //<----Ask for j + k
~sequence = List.new;

p = Pbindef(\iterOp,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)

1
2 3 2
3 4 3 5 4 3
4 5 4 6 5 4 7 6 5 4
5 6 5 7 6 5 8 7 6 5 9 8 7 6 5
(...)

(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
// Make sequence
limit.do{|i|  (i+1).do{|j| (j+1).do{|k|  seq.add((i+1)+(j+1)-(k+1)) }}}; //<----Ask for i + j - k
~sequence = List.new;

p = Pbindef(\iterOp,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)

1
2 1 2
3 2 3 1 2 3
4 3 4 2 3 4 1 2 3 4
(...)

(
var limit = 10;
var dur = 0.15;
var interval = 1;
var seq = List();
// Make sequence
limit.do{|i|  (i+1).do{|j| (j+1).do{|k|  seq.add((i+1)+(k+1)-(j+1)) }}}; //<----Ask for i + k - j
~sequence = List.new;

p = Pbindef(\iterOp,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)


//if n = 2
1
2 3 3 4
3 4 5 4 5 6 5 6 7
4 5 6 7 5 6 7 8 6 7 8 9 7 8 9 10
5 6 7 8 9 6 7 8 9 10 7 8 9 10 11 8 9 10 11 12 9 10 11 12 13
(...)

(
var limit = 10;
var dur = 0.15;
var interval = 1;
var neg = 2;
var seq = List();
// Make sequence
limit.do{|i|  (i+1).do{|j| (j+1).do{|k|  seq.add((i+1)+(j+1)+(k+1) - neg) }}}; //<----Ask for i + j + k - n
~sequence = List.new;

p = Pbindef(\iterOp,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval, inf),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)
