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

                      Counting several things at once
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

//Test
Synth(\sine)


/*_______________________

Simple 2 level count
_________________________*/

1 234
2 234
3 234
4 234
(...)

(
// Structure
var limit = 6; // Level 1 serie
var limit2 = 6; // Level 2 serie
var interval = 4; //Growth interval level 1
var interval2 = 3; //Growth interval level 2
var dur = 0.125;
// Make sequence
var seq;
// First level
seq = Array.series(limit, 0, interval);
seq = seq ! limit2;
// Second level
seq = seq.collect{|item, i| item[0] = i * interval2 };
seq = seq.flatten;
~sequence = List.new;

p = Pbindef(\levelCount1,
	\instrument, \sine,
	\degree, Pseq(seq,inf),
	\scale, ~scale,
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
)

p.play
p.stop
~sequence.postln

/*_________________________________________

Simple 2 level count splitted at beggining
___________________________________________*/

1 1234
2 1234
3 1234
4 1234
(...)

(
// Structure
var limit = 4; // Level 1 serie
var interval = 4; //Growth interval level 1
var dur = 0.12;
// Make sequence
var seq;
var seq2;
// First level
seq = Array.series(limit, 0, interval);
seq2 = seq.copy;
seq = seq ! limit;
// Second level
seq2 = seq.collect{|item, i| [seq2[i]]++item };
seq2 = seq2.flatten;
~sequence = List.new;

p = Pbindef(\levelCount2,
	\instrument, \sine,
	\degree, Pseq(seq2,inf),
	\scale, ~scale,
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
)

p.play
p.stop
~sequence.postln


/*___________________________________

Simple 2 level growth splitted at end
_____________________________________*/

1234 1
1234 2
1234 3
1234 4
(...)

(
// Structure
var limit = 4; // Level 1 serie
var interval = 4; //Growth interval level 1
var dur = 0.2;
// Make sequences
var seq = Array.series(limit, 0, interval);
var seq2 = seq.copy;
seq = seq ! limit;
// Second level
seq2 = seq.collect{|item, i| item++[seq2[i]] };
seq2 = seq2.flatten;
~sequence = List.new;

p = Pbindef(\levelCount3,
	\instrument, \sine,
	\degree, Pseq(seq2,inf),
	\scale, ~scale,
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
)

p.play
p.stop
~sequence.postln


/*_______________________________________

Level growth splitted at end in 3 levels
_________________________________________*/

1234 1 1234 2 1234 3 1234 4 1
1234 1 1234 2 1234 3 1234 4 2
1234 1 1234 2 1234 3 1234 4 3
1234 1 1234 2 1234 3 1234 4 4
(...)

(
// Structure
var limit = 5; // Level 1 serie
var interval = 2; //Growth interval level 1
var interval3 = 1; //Level 3 growth interval
var dur = 0.1;
// Make sequence
var seq;
var seq2;
var seq3;
// First level
seq = Array.series(limit, 0, interval);
seq2 = seq.copy;
seq = seq ! limit;
// Second level
seq2 = seq.collect{|item, i| item++[seq2[i]] };
// Third level
seq3 = limit.collect{|i| seq2++[i*interval3]};
2.do{seq3 = seq3.flatten};
~sequence = List.new;

p = Pbindef(\levelCount4,
	\instrument, \sine,
	\degree, Pseq(seq3,inf),
	\scale, ~scale,
	\amp, ~amp,
	\octave, Pseq([4,5],inf), // <--- A little bit of variation with register shifting.
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\dur, dur
).quant_(0).play;
//seq3.postln; //check
)

p.play
p.stop
~sequence.postln


/*_______________________________________

Level growth merged in 2 levels
_________________________________________*/


1             2               3               4               5

1 2 3 4 3 2 1 | 1 2 3 4 3 2 1 | 1 2 3 4 3 2 1 | 1 2 3 4 3 2 1 | 1 2 3 4 3 2 1 |
1 2 3 4 5 4 3 2 1 | 1 2 3 4 5 4 3 2 1 | 1 2 3 4 5 4 3 2 1 | 1 2 3 4 5 4 3 2 1 |
1                 2                   3                   4


// Here an algorithm to calculate the number of integers in a pair of sequences with difference of 2 items from each other.
// Be aware that this works only for this conditition! To see how this was derived, see Appendix 2.
(
~getSize = { |serieA, serieB |
	var aSize = serieA.size;
	var bSize = serieB.size;
	var bigger, smaller;
	q = abs(aSize - bSize);
	bigger = max(aSize, bSize);
	smaller = min(aSize, bSize);
	s = ceil(bigger/q); //Repetitions of the smallest serie
	b = s - 1; //Repetitions of bigger series (always has a difference of s-1 repetitions)
	x = bigger * b;
	y = smaller * s;
	f = x+y;
});

//This is how it works (series must have 2 items of difference)
a = ~getSize.value([1,2,2],[1,2,3,4,2])

(
// Structure
var limit = 6;
var interval = 4;
var dur = 0.15;
// Make Series
var serA = Array.series(limit, 0, interval);
var serB = Array.series(limit+1, 0, interval);
var finalSize, seq;
serA = serA.mirror;
serB = serB.mirror;
finalSize = ~getSize.value(serA,serB).asInteger; //For this to work you need to evaluate the ~getSize function
seq = [serA,serB].lace(finalSize);
~sequence = List.new;

p = Pbindef(\levelCount5,
	\instrument, \sine,
	\degree, Pseq(seq,inf),
	\scale, ~scale,
	\amp, ~amp,
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }),
	\octave, Pseq([4,5],inf), // <--- A little bit of variation with register shifting.
	\dur, dur
).quant_(0).play;
//seq3.postln; //check
)

p.play
p.stop
~sequence.postln

/*_______________________________________

Canonic growth in 5 levels
_________________________________________*/

1 | | | | 2 | | | | 3 | | | | 4 | | | | 3 | | | | 2 | | | | 1 | | | | | (...)
| | | | | | 1 | | | | 2 | | | | 3 | | | | 4 | | | | 3 | | | | 2 | | | | 1 | | | | | (...)
| | | | | | | | | | | | 1 | | | | 2 | | | | 3 | | | | 4 | | | | 3 | | | | 2 | | | | 1 | | | | | (...)
| | | | | | | | | | | | | | | | | | 1 | | | | 2 | | | | 3 | | | | 4 | | | | 3 | | | | 2 | | | | 1 | | | | | (...)
| | | | | | | | | | | | | | | | | | | | | | | | 1 | | | | 2 | | | | 3 | | | | 4 | | | | 3 | | | | 2 | | | | 1 | | | | | (...)

(
// Structure
var limit = 5;
var interval = 1;
var dur = 0.25;
// Serie
var rests = \ ! (limit - 1); // the \ sign represents a rest in Pbind pitch parameters
var offset = \ ! (limit + 1);
var recorder;
var seq = limit.collect{|i| ([i]*interval)++rests };
var amp = Pfunc({ |ev| if(ev.degree < 1) { 0.1 } { ev.degree.reciprocal * 0.25}});
seq = seq.mirror;
seq = seq.flatten;
offset = offset.flatten;
~sequence = List.new;
recorder = Pfunc({ |ev| ~sequence.add(ev.degree)});

p = Ppar([
	Pbindef(\voice1, \instrument, \sine, \degree, Pseq(seq,inf), \scale, ~scale, \debugger, recorder, \amp, amp, \dur, dur), //voice 1
	Pbindef(\voice2, \instrument, \sine, \degree, Pseq(offset++seq,inf), \scale, ~scale, \debugger, recorder, \amp, amp, \dur, dur), //voice 2
	Pbindef(\voice3, \instrument, \sine, \degree, Pseq(offset++offset++seq,inf), \scale, ~scale, \debugger, recorder, \amp, amp, \dur, dur), //voice 3
	Pbindef(\voice4, \instrument, \sine, \degree, Pseq(offset++offset++offset++seq,inf), \scale, ~scale, \debugger, recorder, \amp, amp, \dur, dur), //voice 4
	Pbindef(\voice5, \instrument, \sine, \degree, Pseq(offset++offset++offset++offset++seq,inf), \scale, ~scale, \debugger, recorder, \amp, amp, \dur, dur), //voice 5
]).play;
)

p.stop;
p.play;
~sequence.postln;


/*____________

 Apendix 2
____________*/


// What do all of these have in common?

1             2               3               4               5

1 2 3 4 3 2 1 | 1 2 3 4 3 2 1 | 1 2 3 4 3 2 1 | 1 2 3 4 3 2 1 | 1 2 3 4 3 2 1 |
1 2 3 4 5 4 3 2 1 | 1 2 3 4 5 4 3 2 1 | 1 2 3 4 5 4 3 2 1 | 1 2 3 4 5 4 3 2 1 |
1                 2                   3                   4

//

1         2           3           4

1 2 3 2 1 | 1 2 3 2 1 | 1 2 3 2 1 | 1 2 3 2 1 |
1 2 3 4 3 2 1 | 1 2 3 4 3 2 1 | 1 2 3 4 3 2 1 |

1             2               3

//

1     2       3

1 2 1 | 1 2 1 | 1 2 1 |
1 2 3 2 1 | 1 2 3 2 1 |

1         2

//

2

1 | 1 |
1 2 1 |

1


x = [1, 2, 3, 4, 5, 4, 3, 2, 1];
y = [1, 2, 3, 4, 3, 2, 1];

x = [1, 2, 3, 4, 3, 2, 1]
y = [1, 2, 3, 2, 1]

x = [1,2,3,2,1]
y = [1,2,1]

x = [1,2,1]
y = [1]

// For regular differences between series this formula can be used:

(
x = [1, 2, 3, 4, 5, 4, 3, 2, 1];
y = [1, 2, 3, 4, 3, 2, 1];
)

(
q  = x.size - y.size;
t = max(x.size, y.size);
s = ceil(t / q); //Repetitions of the smallest serie
k = s - 1; // The biggest serie always has a difference of -1 repetitions in this case
a = max(x.size, y.size) * k; //Biggest amount of integers
b = min(x.size, y.size) * s; //Smallest amount of integers
f = a+b //Final size
)

/* Note to Mr. Johnson */

// In the book, the example given is a bit misleading. The book suggests a serie that goes like:
1 2 3 4 3 2 1
// that keeps repeating until it meets it's full cycle agains another serie that goes:
1 2 3 4 5 4 3 2 1
// It seems to me that, as described, the logical repeati is
1 2 3 4 3 2 1 | 1 2 3 4 3 2 1 |
// and not:
1 2 3 4 3 2 | 1 2 3 4 3 2 1
// since if the later would be the case, the proper formulation at the beggining would be
1 2 3 4 3 2
// What do you think?

// TO DO: What if the difference is not linearly incremental like this? what is a general solution for the problem?

1

1 2 3 2 1 |
1 | 1 | 1 |

1  2   3

1               2

1 2 3 4 5 3 2 1 | 1 2 3 4 5 3 2 1 |
1 2 3 2 1 | 1 2 3 2 1 | 1 2 3 2 1 |

1         2           3

// This results in a fractal structure:
1                         2                           3                           4                           5                           6

1 2 3 4 5 6 7 6 5 4 3 2 1 | 1 2 3 4 5 6 7 6 5 4 3 2 1 | 1 2 3 4 5 6 7 6 5 4 3 2 1 | 1 2 3 4 5 6 7 6 5 4 3 2 1 | 1 2 3 4 5 6 7 6 5 4 3 2 1 | 1 2 3 4 5 6 7 6 5 4 3 2 1 |

1                     2                       3                       4                       5                       6                       7

1 2 3 4 5 6 5 4 3 2 1 | 1 2 3 4 5 6 5 4 3 2 1 | 1 2 3 4 5 6 5 4 3 2 1 | 1 2 3 4 5 6 5 4 3 2 1 | 1 2 3 4 5 6 5 4 3 2 1 | 1 2 3 4 5 6 5 4 3 2 1 | 1 2 3 4 5 6 5 4 3 2 1 |

1         2           3           4           5           6           7           8           9           10          11          12          13                      14

1 2 3 2 1 | 1 2 3 2 1 | 1 2 3 2 1 | 1 2 3 2 1 | 1 2 3 2 1 | 1 2 3 2 1 | 1 2 3 2 1 | 1 2 3 2 1 | 1 2 3 2 1 | 1 2 3 2 1 | 1 2 3 2 1 | 1 2 3 2 1 | 1 2 3 2 1 | 1 2 3 2 1 |

1     2       3       4       5       6       7       8       9       10      11      12      13      14      15     16       17      18     19      20      21      22

1 2 1 | 1 2 1 | 1 2 1 | 1 2 1 | 1 2 1 | 1 2 1 | 1 2 1 | 1 2 1 | 1 2 1 | 1 2 1 | 1 2 1 | 1 2 1 | 1 2 1 | 1 2 1 | 1 2 1 | 1 2 1 | 1 2 1 | 1 2 1 | 1 2 1 | 1 2 1 | 1 2 1 |

1 2 3 4 5 6 7 8 9 (...)

1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 | 1 |

