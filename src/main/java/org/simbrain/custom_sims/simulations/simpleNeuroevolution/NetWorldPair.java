package org.simbrain.custom_sims.simulations.simpleNeuroevolution;

import org.simbrain.custom_sims.helper_classes.NetworkWrapper;
import org.simbrain.custom_sims.helper_classes.OdorWorldWrapper;
import org.simbrain.custom_sims.helper_classes.Simulation;
import org.simbrain.network.core.Neuron;
import org.simbrain.network.groups.NeuronGroup;
import org.simbrain.world.odorworld.entities.OdorWorldEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NetWorldPair {
    public static int netSize;
    public static Simulation sim;

    long netSeed;
    NetworkWrapper net;
    int generation;


    List<NeuronGroup> input = new ArrayList<>();
    List<Neuron> inputFlat = new ArrayList<>();
    List<Neuron> hidden = new ArrayList<>();
    NeuronGroup output;
    List<Neuron> sourceNeuron = new ArrayList<>();
    List<Neuron> targetNeuron = new ArrayList<>();
    OdorWorldWrapper world;
    OdorWorldEntity mouse;
    OdorWorldEntity cheese;
    OdorWorldEntity poison;
    NetWorldPairAttribute attriubte;


    public NetWorldPair(int netIndex, int x, int y, int width, int height, long seed) {
        initializeNetwork(netIndex, x, y, width, height, seed);
    }

    public void initializeNetwork(int netIndex, int x, int y, int width, int height, long seed) {
        Random rand = new Random(seed + netIndex);
        long thisSeed = rand.nextLong();

        int prefixDigit = (int) (Math.log10(netSize) + 1);
        int prefix = (int) ((generation + 1) * Math.pow(10, prefixDigit));
        netSeed = thisSeed;

        attriubte = new NetWorldPairAttribute(prefix + netIndex, netIndex, x, y, width, height);
        net = (sim.addNetwork(x, y, width, height, "N" + attriubte.getNetID()));
    }

    //public void

    public NetworkWrapper getNet() {
        return net;
    }

    public OdorWorldWrapper getWorld() {
        return world;
    }


}