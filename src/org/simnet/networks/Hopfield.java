/*
 * Created on Sep 13, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.simnet.networks;

import org.simnet.interfaces.*;
import org.simnet.interfaces.Neuron;
import org.simnet.neurons.*;
import org.simnet.neurons.rules.*;
import org.simnet.synapses.*;

import java.util.*;

/**
 * @author yoshimi
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Hopfield extends Network {

	public Hopfield() {
	}
	
	public Hopfield(int numNeurons) {
		//Create the neurons
		for(int i = 0; i < numNeurons; i++) {
			BinaryNeuron n = new BinaryNeuron();
			addNeuron(n);
		}
		
		//Create full symmetric connections without self-connections
		for(int i = 0; i < numNeurons; i++) {
			for(int j = 0; j < i; j++) {
				StandardSynapse w = new StandardSynapse();
				w.setUpperBound(1);
				w.setLowerBound(-1);
				w.randomize();
				w.setStrength(Network.round(w.getStrength(), 0));
				w.setSource(this.getNeuron(i));
				w.setTarget(this.getNeuron(j));
				addWeight(w);
				
				StandardSynapse w2 = new StandardSynapse();
				w2.setUpperBound(1);
				w2.setLowerBound(-1);
				w2.setStrength(w.getStrength());
				w2.setSource(this.getNeuron(j));
				w2.setTarget(this.getNeuron(i));
				addWeight(w2);

			}
			
		}
				
	}
	
	public void randomizeWeights() {
		for(int i = 0; i < getNeuronCount(); i++) {
			for(int j = 0; j < i; j++) {
				Synapse w = Network.getWeight(getNeuron(i), getNeuron(j));
				w.randomize();
				w.setStrength(Network.round(w.getStrength(), 0));

				Synapse w2 = Network.getWeight(getNeuron(j), getNeuron(i));
				w2.setStrength(w.getStrength());
			}
			
		}
	}
	
	public void train() {
		
		//Assumes all neurons have the same upper and lower values
		double low = getNeuron(0).getLowerBound();
		double hi = getNeuron(0).getUpperBound();
		
		for(int i = 0; i < this.getWeightCount(); i++) {
			//Must use buffer
			Synapse w = this.getWeight(i);
			Neuron src = w.getSource();
			Neuron tar = w.getTarget();
			w.setStrength(	w.getStrength() +  
							(( 2 * src.getActivation() - hi - low) / (hi - low)) *
							(( 2 * tar.getActivation() - hi - low) / (hi - low)));
			
		}
	}
	
	/**
	 * Update nodes randomly
	 * 
	 */
	public void update() {		
		int num = getNeuronCount();
		java.util.Random ran = new java.util.Random();
		ran.setSeed(4123123);
		
		int j;
		
		for (int i = 0; i < num; i++) {
			
			j = (int)(ran.nextDouble() * num);
			Neuron n = (Neuron) neuronList.get(i);
			n.update();		// update neuron buffers
			n.commitBuffer();
		}
	}
}
