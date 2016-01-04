
// SuperCollider has generative-grammar facilites, one is in the NatureToolkit Quark.
// I however, as usual, prefered for this to illustrate the process instead of using ready-made solutions.
(
~conversor = { |input|
	var output;
	// Change it to whatever else if you want (hz, midinotes, etc)
	switch(input)
	{"a"} {output = 5}
	{"b"} {output = 6}
	{"c"} {output = 0}
	{"d"} {output = 1}
	{"e"} {output = 2};
	output
};

~generation = { |input|
	var output;
	// Our rules:
	switch(input)
	{"a"} {output = "be"}
	{"b"} {output = "ad"}
	{"c"} {output = "c"}
	{"d"} {output = "a"}
	{"e"} {output = "dc"};
	output
};

~stringMaker = { |input|
	var transforms = List();
	input.do{|i|
		var new = ~generation.(i.asString);
		new.do{|i| transforms.add(i)}
	};
	transforms;
};

~sequenceMaker = { |input, iterations = 1|
	var output = List();
	var final;
	var x;
	iterations.do{
		x = ~stringMaker.(input);
		output.add(x);
		input = x;
	};
	output = output.flatten;
};
)

// This is our final transforming gadget:
~sequenceMaker.("a", 3)

// Transforming by finite automaton
a->be
b->ad
c->c
d->a
e->dc

(
var start = "a";
var limit = 5;
var dur = 0.15;
var interval = 1;
var seq = ~sequenceMaker.(start, limit).collect{|i| ~conversor.(i.asString)};
var repetitions = seq.size;

p = Pbindef(\automata1,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval,1),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, dur
).quant_(0).play
)

// For this one we need to change the rules and the decoding:
a->bcac
b->acbd
c->abar
d->bcbd
r->rrrr
(
~generation = { |input|
	var output;
	// Our rules:
	switch(input)
	{"a"} {output = "bcac"}
	{"b"} {output = "acbd"}
	{"c"} {output = "abar"}
	{"d"} {output = "bcbd"}
	{"r"} {output = "rrrr"};
	output
};

~conversor = { |input|
	var output;
	// Change it to whatever else if you want (hz, midinotes, etc)
	switch(input)
	{"a"} {output = 5}
	{"b"} {output = 6}
	{"c"} {output = 0}
	{"d"} {output = 1}
	{"r"} {output = \}; // backslash is shortcut for a REST in SC.
	output
};
)

(
var start = "a";
var limit = 5;
var dur = 0.15;
var interval = 1;
var seq = ~sequenceMaker.(start, limit).collect{|i| ~conversor.(i.asString)};
var repetitions = seq.size;

p = Pbindef(\automata2,
	\instrument, \sine,
	\scale, ~scale,
	\degree, Pseq(seq * interval,1),
	\recorder, Pfunc({ |ev| ~sequence.add(ev.degree) }), // To store the resulting sequence
	\amp, ~amp, // To compensate for higher frequencies perceived as louder
	\dur, Pfunc({ |ev| ev.degree.postln; 0})
).quant_(0).play
)

// The book has 2 more examples, I do not include them however because basically all you have to do is to change
// the rules and the conversor :)

