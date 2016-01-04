/*_____________________________________________________________

dbLib [additions to SuperCollider]

< Report general amplitude >
Copyright (C) <2015>

by Darien Brito
http://www.darienbrito.com

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

________________________________________________________________*/


PeakAmpTrig  {
	classvar peakSynth, peakFunc, activeSynth, oscFunc, net, osc;

	*activate { |numChannels = 2, hostname = "localhost", port = 9000, threshold = 0.8, band = \low, cFreq  = 500, post = false |
		fork{
			peakFunc = {
				{
					activeSynth = Synth(\peakAmp,
						target: RootNode(Server.default),
						addAction: \addToTail
					);
				}.defer(0.01)
			};
			// Synths per band
			switch( band )
			{ \low } {
				peakSynth = SynthDef(\peakAmp,{
					var input = In.ar(0, numChannels);
					input = LPF.ar(input, cFreq);
					SendPeakRMS.ar(input, 100, 1, '/peakRMS');
				}).add;
			}
			{ \mid } {
				peakSynth = SynthDef(\peakAmp,{
					var input = In.ar(0, numChannels);
					input = BPF.ar(input, cFreq);
					SendPeakRMS.ar(input, 100, 1, '/peakRMS');
				}).add;
			}
			{ \hi } {
				peakSynth = SynthDef(\peakAmp,{
					var input = In.ar(0, numChannels);
					input = HPF.ar(input, cFreq);
					SendPeakRMS.ar(input, 100, 1, '/peakRMS');
				}).add;
			};

			Server.default.sync;
			peakFunc.value;
			net = NetAddr(hostname, port);
			oscFunc = {
				osc = OSCFunc({ |msg|
					if(msg[3] > threshold) {
					net.sendMsg('/peak', 1)
					} { net.sendMsg('/peak', 0)}
				}, '/peakRMS');
			};
			oscFunc.value;
			CmdPeriod.add(peakFunc); //Evaluate on cmd + .
			CmdPeriod.add(oscFunc);
			"PeakAmp active".postln;
		}
	}

	*deactivate {
		activeSynth.free;
		osc.free;
		CmdPeriod.remove(peakFunc);
		CmdPeriod.remove(oscFunc);
		"PeakAmp inactive...".postln;
	}

}



PeakAmpReport  {
	classvar peakSynth, peakFunc, activeSynth, oscFunc, net, osc;

	*activate { |numChannels = 2, hostname = "localhost", port = 9000, post = false |
		fork
		{
			peakFunc =
			{
				{
					activeSynth = Synth(\peakAmpReport,
						target: RootNode(Server.default),
						addAction: \addToTail
					);
				}.defer(0.01)
			};
			peakSynth = SynthDef(\peakAmpReport,{
				var input = In.ar(0, numChannels);
				SendPeakRMS.ar(input, 100, 1, '/peakRMSReport');
			}).add;
			Server.default.sync;
			peakFunc.value;
			net = NetAddr(hostname, port);
			oscFunc = {
				osc = OSCFunc({ |msg|
					if(post){
						"peak: %".format(msg[3]).postln;
					};
					net.sendMsg('/peakReport', msg[3])
				}, '/peakRMSReport');
			};
			oscFunc.value;
			CmdPeriod.add(peakFunc); //Evaluate on cmd + .
			CmdPeriod.add(oscFunc);
			"PeakAmpReport active".postln;
		}
	}

	*deactivate {
		activeSynth.free;
		osc.free;
		CmdPeriod.remove(peakFunc);
		CmdPeriod.remove(oscFunc);
		"PeakAmpReport inactive...".postln;
	}

}
