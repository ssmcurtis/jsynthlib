/*
 * JFugue - API for Music Programming
 * Copyright (C) 2003-2008  David Koelle
 *
 * http://www.jfugue.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.jfugue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jfugue.elements.Controller;
import org.jfugue.elements.Instrument;
import org.jfugue.elements.Tempo;

/**
 * Loads default definitions into the JFugue dictionary.
 * This allows users to refer to instruments, percussion sounds,
 * and controller events by easy-to-remember names.
 *
 * <p>
 * Dictionary items can be added via the Music String.  See
 * the documentation for more information.
 * </p>
 *
 *@author David Koelle
 *@version 2.0
 */
public final class JFugueDefinitions
{
    private JFugueDefinitions() { }

    /**
     * This is apparently the default {@link javax.sound.midi.Sequence} resolution.
     * <p>TODO Is there any way to get the actual resolution?
     */
    public static final double SEQUENCE_RESOLUTION = 128.0;
    
    /**
     * Loads default definitions into the JFugue dictionary.  This includes
     * all of the string representations for instrument names, percussion sounds,
     * controller events, and some controller values.
     * @param dictionaryMap the dictionary instantiated by the parser
     */
    public static void populateDictionary(Map<String, String> dictionaryMap)
    {
    	dictionaryMap.putAll(DICT_MAP);
    }
    
    /**
     * @deprecated
     */
    @SuppressWarnings("unused")
	private static final String[][] DICT_ARRAY = new String[][] {
        //
        // Instrument names
        //
        {"PIANO"                    ,"0"},
        {"ACOUSTIC_GRAND"           ,"0"},
        {"BRIGHT_ACOUSTIC"          ,"1"},
        {"ELECTRIC_GRAND"           ,"2"},
        {"HONKEY_TONK"              ,"3"},
        {"ELECTRIC_PIANO"           ,"4"},
        {"ELECTRIC_PIANO_1"         ,"4"},
        {"ELECTRIC_PIANO_2"         ,"5"},
        {"HARPISCHORD"              ,"6"},
        {"CLAVINET"                 ,"7"},
        {"CELESTA"                  ,"8"},
        {"GLOCKENSPIEL"             ,"9"},

        {"MUSIC_BOX"                ,"10"},
        {"VIBRAPHONE"               ,"11"},
        {"MARIMBA"                  ,"12"},
        {"XYLOPHONE"                ,"13"},
        {"TUBULAR_BELLS"            ,"14"},
        {"DULCIMER"                 ,"15"},
        {"DRAWBAR_ORGAN"            ,"16"},
        {"PERCUSSIVE_ORGAN"         ,"17"},
        {"ROCK_ORGAN"               ,"18"},
        {"CHURCH_ORGAN"             ,"19"},

        {"REED_ORGAN"               ,"20"},
        {"ACCORDIAN"                ,"21"},
        {"HARMONICA"                ,"22"},
        {"TANGO_ACCORDIAN"          ,"23"},
        {"GUITAR"                   ,"24"},
        {"NYLON_STRING_GUITAR"      ,"24"},
        {"STEEL_STRING_GUITAR"      ,"25"},
        {"ELECTRIC_JAZZ_GUITAR"     ,"26"},
        {"ELECTRIC_CLEAN_GUITAR"    ,"27"},
        {"ELECTRIC_MUTED_GUITAR"    ,"28"},
        {"OVERDRIVEN_GUITAR"        ,"29"},

        {"DISTORTION_GUITAR"        ,"30"},
        {"GUITAR_HARMONICS"         ,"31"},
        {"ACOUSTIC_BASS"            ,"32"},
        {"ELECTRIC_BASS_FINGER"     ,"33"},
        {"ELECTRIC_BASS_PICK"       ,"34"},
        {"FRETLESS_BASS"            ,"35"},
        {"SLAP_BASS_1"              ,"36"},
        {"SLAP_BASS_2"              ,"37"},
        {"SYNTH_BASS_1"             ,"38"},
        {"SYNTH_BASS_2"             ,"39"},

        {"VIOLIN"                   ,"40"},
        {"VIOLA"                    ,"41"},
        {"CELLO"                    ,"42"},
        {"CONTRABASS"               ,"43"},
        {"TREMOLO_STRINGS"          ,"44"},
        {"PIZZICATO_STRINGS"        ,"45"},
        {"ORCHESTRAL_STRINGS"       ,"46"},
        {"TIMPANI"                  ,"47"},
        {"STRING_ENSEMBLE_1"        ,"48"},
        {"STRING_ENSEMBLE_2"        ,"49"},

        {"SYNTH_STRINGS_1"          ,"50"},
        {"SYNTH_STRINGS_2"          ,"51"},
        {"CHOIR_AAHS"               ,"52"},
        {"VOICE_OOHS"               ,"53"},
        {"SYNTH_VOICE"              ,"54"},
        {"ORCHESTRA_HIT"            ,"55"},
        {"TRUMPET"                  ,"56"},
        {"TROMBONE"                 ,"57"},
        {"TUBA"                     ,"58"},
        {"MUTED_TRUMPET"            ,"59"},

        {"FRENCH_HORN"              ,"60"},
        {"BRASS_SECTION"            ,"61"},
        {"SYNTHBRASS_1"             ,"62"},
        {"SYNTH_BRASS_1"            ,"62"},
        {"SYNTHBRASS_2"             ,"63"},
        {"SYNTH_BRASS_2"            ,"63"},
        {"SOPRANO_SAX"              ,"64"},
        {"ALTO_SAX"                 ,"65"},
        {"TENOR_SAX"                ,"66"},
        {"BARITONE_SAX"             ,"67"},
        {"OBOE"                     ,"68"},
        {"ENGLISH_HORN"             ,"69"},

        {"BASSOON"                  ,"70"},
        {"CLARINET"                 ,"71"},
        {"PICCOLO"                  ,"72"},
        {"FLUTE"                    ,"73"},
        {"RECORDER"                 ,"74"},
        {"PAN_FLUTE"                ,"75"},
        {"BLOWN_BOTTLE"             ,"76"},
        {"SKAKUHACHI"               ,"77"},
        {"WHISTLE"                  ,"78"},
        {"OCARINA"                  ,"79"},

        {"LEAD_SQUARE"              ,"80"},
        {"SQUARE"                   ,"80"},
        {"LEAD_SAWTOOTH"            ,"81"},
        {"SAWTOOTH"                 ,"81"},
        {"LEAD_CALLIOPE"            ,"82"},
        {"CALLIOPE"                 ,"82"},
        {"LEAD_CHIFF"               ,"83"},
        {"CHIFF"                    ,"83"},
        {"LEAD_CHARANG"             ,"84"},
        {"CHARANG"                  ,"84"},
        {"LEAD_VOICE"               ,"85"},
        {"VOICE"                    ,"85"},
        {"LEAD_FIFTHS"              ,"86"},
        {"FIFTHS"                   ,"86"},
        {"LEAD_BASSLEAD"            ,"87"},
        {"BASSLEAD"                 ,"87"},
        {"PAD_NEW_AGE"              ,"88"},
        {"NEW_AGE"                  ,"88"},
        {"PAD_WARM"                 ,"89"},
        {"WARM"                     ,"89"},

        {"PAD_POLYSYNTH"            ,"90"},
        {"POLYSYNTH"                ,"90"},
        {"PAD_CHOIR"                ,"91"},
        {"CHOIR"                    ,"91"},
        {"PAD_BOWED"                ,"92"},
        {"BOWED"                    ,"92"},
        {"PAD_METALLIC"             ,"93"},
        {"METALLIC"                 ,"93"},
        {"PAD_HALO"                 ,"94"},
        {"HALO"                     ,"94"},
        {"PAD_SWEEP"                ,"95"},
        {"SWEEP"                    ,"95"},
        {"FX_RAIN"                  ,"96"},
        {"RAIN"                     ,"96"},
        {"FX_SOUNDTRACK"            ,"97"},
        {"SOUNDTRACK"               ,"97"},
        {"FX_CRYSTAL"               ,"98"},
        {"CRYSTAL"                  ,"98"},
        {"FX_ATMOSPHERE"            ,"99"},
        {"ATMOSPHERE"               ,"99"},

        {"FX_BRIGHTNESS"            ,"100"},
        {"BRIGHTNESS"               ,"100"},
        {"FX_GOBLINS"               ,"101"},
        {"GOBLINS"                  ,"101"},
        {"FX_ECHOES"                ,"102"},
        {"ECHOES"                   ,"102"},
        {"FX_SCI-FI"                ,"103"},
        {"SCI-FI"                   ,"103"},
        {"SITAR"                    ,"104"},
        {"BANJO"                    ,"105"},
        {"SHAMISEN"                 ,"106"},
        {"KOTO"                     ,"107"},
        {"KALIMBA"                  ,"108"},
        {"BAGPIPE"                  ,"109"},

        {"FIDDLE"                   ,"110"},
        {"SHANAI"                   ,"111"},
        {"TINKLE_BELL"              ,"112"},
        {"AGOGO"                    ,"113"},
        {"STEEL_DRUMS"              ,"114"},
        {"WOODBLOCK"                ,"115"},
        {"TAIKO_DRUM"               ,"116"},
        {"MELODIC_TOM"              ,"117"},
        {"SYNTH_DRUM"               ,"118"},
        {"REVERSE_CYMBAL"           ,"119"},

        {"GUITAR_FRET_NOISE"        ,"120"},
        {"BREATH_NOISE"             ,"121"},
        {"SEASHORE"                 ,"122"},
        {"BIRD_TWEET"               ,"123"},
        {"TELEPHONE_RING"           ,"124"},
        {"HELICOPTER"               ,"125"},
        {"APPLAUSE"                 ,"126"},
        {"GUNSHOT"                  ,"127"},

        //
        // Percussion names
        //
        {"ACOUSTIC_BASS_DRUM"     ,"35"},
        {"BASS_DRUM"              ,"36"},
        {"SIDE_STICK"             ,"37"},
        {"ACOUSTIC_SNARE"         ,"38"},
        {"HAND_CLAP"              ,"39"},

        {"ELECTRIC_SNARE"         ,"40"},
        {"LOW_FLOOR_TOM"          ,"41"},
        {"CLOSED_HI_HAT"          ,"42"},
        {"HIGH_FLOOR_TOM"         ,"43"},
        {"PEDAL_HI_HAT"           ,"44"},
        {"LOW_TOM"                ,"45"},
        {"OPEN_HI_HAT"            ,"46"},
        {"LOW_MID_TOM"            ,"47"},
        {"HI_MID_TOM"             ,"48"},
        {"CRASH_CYMBAL_1"         ,"49"},

        {"HIGH_TOM"               ,"50"},
        {"RIDE_CYMBAL_1"          ,"51"},
        {"CHINESE_CYMBAL"         ,"52"},
        {"RIDE_BELL"              ,"53"},
        {"TAMBOURINE"             ,"54"},
        {"SPLASH_CYMBAL"          ,"55"},
        {"COWBELL"                ,"56"},
        {"CRASH_CYMBAL_2"         ,"57"},
        {"VIBRASLAP"              ,"58"},
        {"RIDE_CYMBAL_2"          ,"59"},

        {"HI_BONGO"               ,"60"},
        {"LOW_BONGO"              ,"61"},
        {"MUTE_HI_CONGA"          ,"62"},
        {"OPEN_HI_CONGA"          ,"63"},
        {"LOW_CONGA"              ,"64"},
        {"HIGH_TIMBALE"           ,"65"},
        {"LOW_TIMBALE"            ,"66"},
        {"HIGH_AGOGO"             ,"67"},
        {"LOW_AGOGO"              ,"68"},
        {"CABASA"                 ,"69"},

        {"MARACAS"                ,"70"},
        {"SHORT_WHISTLE"          ,"71"},
        {"LONG_WHISTLE"           ,"72"},
        {"SHORT_GUIRO"            ,"73"},
        {"LONG_GUIRO"             ,"74"},
        {"CLAVES"                 ,"75"},
        {"HI_WOOD_BLOCK"          ,"76"},
        {"LOW_WOOD_BLOCK"         ,"77"},
        {"MUTE_CUICA"             ,"78"},
        {"OPEN_CUICA"             ,"79"},

        {"MUTE_TRIANGLE"          ,"80"},
        {"OPEN_TRIANGLE"          ,"81"},

        //
        // Controller names
        //
        {"BANK_SELECT_COARSE"          ,"0"},
        {"MOD_WHEEL_COARSE"            ,"1"},
        {"BREATH_COARSE"               ,"2"},
        {"FOOT_PEDAL_COARSE"           ,"4"},
        {"PORTAMENTO_TIME_COARSE"      ,"5"},
        {"DATA_ENTRY_COARSE"           ,"6"},
        {"VOLUME_COARSE"               ,"7"},
        {"BALANCE_COARSE"              ,"8"},
        {"PAN_POSITION_COARSE"         ,"10"},
        {"EXPRESSION_COARSE"           ,"11"},
        {"EFFECT_CONTROL_1_COARSE"     ,"12"},
        {"EFFECT_CONTROL_2_COARSE"     ,"13"},

        {"SLIDER_1"                    ,"16"},
        {"SLIDER_2"                    ,"17"},
        {"SLIDER_3"                    ,"18"},
        {"SLIDER_4"                    ,"19"},

        {"BANK_SELECT_FINE"            ,"32"},
        {"MOD_WHEEL_FINE"              ,"33"},
        {"BREATH_FINE"                 ,"34"},
        {"FOOT_PEDAL_FINE"             ,"36"},
        {"PORTAMENTO_TIME_FINE"        ,"37"},
        {"DATA_ENTRY_FINE"             ,"38"},
        {"VOLUME_FINE"                 ,"39"},
        {"BALANCE_FINE"                ,"40"},
        {"PAN_POSITION_FINE"           ,"42"},
        {"EXPRESSION_FINE"             ,"43"},
        {"EFFECT_CONTROL_1_FINE"       ,"44"},
        {"EFFECT_CONTROL_2_FINE"       ,"45"},

        {"HOLD_PEDAL"                  ,"64"},
        {"HOLD"                        ,"64"},
        {"PORTAMENTO"                  ,"65"},
        {"SUSTENUTO_PEDAL"             ,"66"},
        {"SUSTENUTO"                   ,"66"},
        {"SOFT_PEDAL"                  ,"67"},
        {"SOFT"                        ,"67"},
        {"LEGATO_PEDAL"                ,"68"},
        {"LEGATO"                      ,"68"},
        {"HOLD_2_PEDAL"                ,"69"},
        {"HOLD_2"                      ,"69"},

        {"SOUND_VARIATION"             ,"70"},
        {"VARIATION"                   ,"70"},
        {"SOUND_TIMBRE"                ,"71"},
        {"TIMBRE"                      ,"71"},
        {"SOUND_RELEASE_TIME"          ,"72"},
        {"RELEASE_TIME"                ,"72"},
        {"SOUND_ATTACK_TIME"           ,"73"},
        {"ATTACK_TIME"                 ,"73"},
        {"SOUND_BRIGHTNESS"            ,"74"},
        {"BRIGHTNESS"                  ,"74"},
        {"SOUND_CONTROL_6"             ,"75"},
        {"CONTROL_6"                   ,"75"},
        {"SOUND_CONTROL_7"             ,"76"},
        {"CONTROL_7"                   ,"76"},
        {"SOUND_CONTROL_8"             ,"77"},
        {"CONTROL_8"                   ,"77"},
        {"SOUND_CONTROL_9"             ,"78"},
        {"CONTROL_9"                   ,"78"},
        {"SOUND_CONTROL_10"            ,"79"},
        {"CONTROL_10"                  ,"79"},

        {"GENERAL_PURPOSE_BUTTON_1"    ,"80"},
        {"GENERAL_BUTTON_1"            ,"80"},
        {"BUTTON_1"                    ,"80"},
        {"GENERAL_PURPOSE_BUTTON_2"    ,"81"},
        {"GENERAL_BUTTON_2"            ,"81"},
        {"BUTTON_2"                    ,"81"},
        {"GENERAL_PURPOSE_BUTTON_3"    ,"82"},
        {"GENERAL_BUTTON_3"            ,"82"},
        {"BUTTON_3"                    ,"82"},
        {"GENERAL_PURPOSE_BUTTON_4"    ,"83"},
        {"GENERAL_BUTTON_4"            ,"83"},
        {"BUTTON_4"                    ,"83"},

        {"EFFECTS_LEVEL"               ,"91"},
        {"EFFECTS"                     ,"91"},
        {"TREMULO_LEVEL"               ,"92"},
        {"TREMULO"                     ,"92"},
        {"CHORUS_LEVEL"                ,"93"},
        {"CHORUS"                      ,"93"},
        {"CELESTE_LEVEL"               ,"94"},
        {"CELESTE"                     ,"94"},
        {"PHASER_LEVEL"                ,"95"},
        {"PHASER"                      ,"95"},

        {"DATA_BUTTON_INCREMENT"       ,"96"},
        {"DATA_BUTTON_INC"             ,"96"},
        {"BUTTON_INC"                  ,"96"},
        {"DATA_BUTTON_DECREMENT"       ,"97"},
        {"DATA_BUTTON_DEC"             ,"97"},
        {"BUTTON_DEC"                  ,"97"},

        {"NON_REGISTERED_COARSE"       ,"98"},
        {"NON_REGISTERED_FINE"         ,"99"},
        {"REGISTERED_COARSE"           ,"100"},
        {"REGISTERED_FINE"             ,"101"},

        {"ALL_SOUND_OFF"               ,"120"},
        {"ALL_CONTROLLERS_OFF"         ,"121"},
        {"LOCAL_KEYBOARD"              ,"122"},
        {"ALL_NOTES_OFF"               ,"123"},
        {"OMNI_MODE_OFF"               ,"124"},
        {"OMNI_OFF"                    ,"124"},
        {"OMNI_MODE_ON"                ,"125"},
        {"OMNI_ON"                     ,"125"},
        {"MONO_OPERATION"              ,"126"},
        {"MONO"                        ,"126"},
        {"POLY_OPERATION"              ,"127"},
        {"POLY"                        ,"127"},

        //
        // Combined Controller names
        // {index = coarse_controller_index * 128 + fine_controller_index)
        //
        {"BANK_SELECT"                ,"16383"},
        {"MOD_WHEEL"                  ,"161"},
        {"BREATH"                     ,"290"},
        {"FOOT_PEDAL"                 ,"548"},
        {"PORTAMENTO_TIME"            ,"677"},
        {"DATA_ENTRY"                 ,"806"},
        {"VOLUME"                     ,"935"},
        {"BALANCE"                    ,"1064"},
        {"PAN_POSITION"               ,"1322"},
        {"EXPRESSION"                 ,"1451"},
        {"EFFECT_CONTROL_1"           ,"1580"},
        {"EFFECT_CONTROL_2"           ,"1709"},
        {"NON_REGISTERED"             ,"12770"},
        {"REGISTERED"                 ,"13028"},

        //
        // Values for controllers
        //
        {"ON"                         ,"127"},
        {"OFF"                        ,"0"},
        {"DEFAULT"                    ,"64"},

        //
        // Tempo values
        // {NEW for JFugue 4.0)
        {"GRAVE"                      ,"40"},
        {"LARGO"                      ,"45"},
        {"LARGHETTO"                  ,"50"},
        {"LENTO"                      ,"55"},
        {"ADAGIO"                     ,"60"},
        {"ADAGIETTO"                  ,"65"},

        {"ANDANTE"                    ,"70"},
        {"ANDANTINO"                  ,"80"},
        {"MODERATO"                   ,"95"},
        {"ALLEGRETTO"                 ,"110"},

        {"ALLEGRO"                    ,"120"},
        {"VIVACE"                     ,"145"},
        {"PRESTO"                     ,"180"},
        {"PRETISSIMO"                 ,"220"},
    };

    /**
     * The default dictionary map.
     */
    public static final Map<String, String> DICT_MAP = initDict();
 
    /**
     * This will eventually replace the static statements.  The purpose of this is to allow
     * JFugueElements to define their own constants instead of sticking them all in here.
     * I expect a very small performance hit on startup but not noticeable.
     * 
     * @return the dictionary
     */
    private static Map<String, String> initDict() {
       	Map<String, String> map = new HashMap<String, String>(Instrument.DICT_MAP.size()
       			+ Controller.DICT_MAP.size() + Tempo.DICT_MAP.size());
    	map.putAll(Instrument.DICT_MAP);
    	map.putAll(Controller.DICT_MAP);
       	map.putAll(Tempo.DICT_MAP);
    	return Collections.unmodifiableMap(map);
	}
}