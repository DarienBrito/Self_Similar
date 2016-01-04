// Convenience class for routing audio

AudioRouter {
	var in, out;

	*new { |input =  "Built-in Microph", output = "Built-in Output"|
		^super.new.init(input, output)
	}

	init {|in_, out_|
		in = in_;
		out = out_;
		this.setRoutes(in, out);
	}

	*devices {
		var ins, outs;
		ins = ServerOptions.inDevices;
		outs = ServerOptions.outDevices;
		"In devices: ".postln;
		ins.postln;
		"Out devices:".postln;
		outs.postln;
	}

	setRoutes {|in, out|
		Server.default.options.inDevice_(in);
		Server.default.options.outDevice_(out);

	}

}