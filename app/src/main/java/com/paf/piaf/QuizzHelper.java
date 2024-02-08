package com.paf.piaf;

import static java.lang.Integer.min;
import static java.util.Collections.shuffle;
import static java.util.Collections.nCopies;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class QuizzHelper {
    private List<Sound> sounds;
    private Sound selectedSound;
    private final Random rand;

    QuizzHelper(List<Sound> sounds) {
        rand = new Random();
        this.sounds = sounds;
        setSelectedSound();
    }

    private void setSelectedSound() {
        List<Integer> indices = new ArrayList<>();
        sounds.forEach((s) -> {
            indices.addAll(nCopies(s.getLevel().getId(),sounds.lastIndexOf(s)));
        });
        selectedSound = sounds.get(indices.get(rand.nextInt(indices.size())));
    }

    public void setSounds(List<Sound> sounds) {
        this.sounds = sounds;
        setSelectedSound();
    }

    public Sound getSelectedSound() {
        return selectedSound;
    }

    public List<Bird> getBirds(int nbChoices) {
        Set<Bird> hBirds =  sounds.stream()
                .filter((s) -> (!s.getBird().equals(selectedSound.getBird())))
                .map(Sound::getBird)
                .collect(Collectors.toSet());
        List<Bird> lBirds = hBirds.stream()
                .collect(Collectors.toList());
        shuffle(lBirds);
        List<Bird> selectedBirds = lBirds.subList(0,min(nbChoices-1,lBirds.size()-1));
        selectedBirds.add(selectedSound.getBird());
        shuffle(selectedBirds);
        return selectedBirds;
    }
}
