/*
 * Copyright 2005 Jeff Weber
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package synthdrivers.BehringerFCB1010;

/** Constants class for the Behringer FCB1010
* 
* @author Jeff Weber
*/
final class Constants {
    
    /** Manufacturer of device*/
    static final String MANUFACTURER_NAME = "Behringer";
    /** Name of device*/
    static final String DEVICE_NAME = "FCB1010";
    /** Name of Converter for device*/
    static final String CONVERTER_NAME = "Behringer FCB1010 Native Dump Converter";
    /** Universal Device Inquiry - does not apply to Behringer FCB1010 */
    static final String INQUIRY_ID = "F0..F7";
    /** Text displayed in the synth driver device details window in preferences*/
    static final String INFO_TEXT = "Device for Behringer FCB1010. Sending and receiving patches from the FCB1010 must be done by hitting the footswitches on the device. First of all, the unit must be in Config mode. To place the unit in Config mode, power the unit on while holding down the Down switch. Then hit the Up switch twice (at this point the Config led should be lit).\n\nTo receive a patch from the unit, select JSynthLib's Get... command to bring up the Get Sysex Data dialog. Hit the Get button in the dialog and then hit Sysex Send (footswitch 6) on the device. To send a patch to the device, first hit Sysex Rcv (footswitch 7) on the device and then use JSynthLib's Send command.\n\nThe FCB1010 Editor can be used to edit any of the settings for the 100 patch locations in the device, as well as the global MIDI channels for program change, control change, expression pedals, and note functions. However, the global settings for Switch 1, Switch 2, MIDI merge, direct select, and running status are not accessible through MIDI. These settings must be edited on the front panel of the FCB1010. See your FCB1010 manual for details";
    
    /** Author of this Driver*/
    static final String AUTHOR = "Jeff Weber";
    
    /** Dump Header Size */
    static final int HDR_SIZE = 7;
    /** Size of native patch used by converter (not including header and stop byte)*/
    static final int FCB1010_NATIVE_SIZE = 2344;
    /** Size of unnibblized patch used by driver (not including header and stop byte)*/
    static final int FCB1010_SIZE = 2051;
    
    /** Offset of the patch name in the sysex record (includes the sysex header).
        * The FCB1010 does not use patch name, so this is set to zero.*/
    static final int PATCH_NAME_START = 0;
    /** Patch Name--Size in bytes.
        * The FCB1010 does not use patch name, so this is set to zero.*/
    static final int PATCH_NAME_SIZE = 0;
    /** Offset of the device ID in the sysex record--Not used by FCB1010*/
    static final int DEVICE_ID_OFFSET = 0;
    
    /** List of bank numbers for driver.
        * The FCB1010 does not use banks, so this is set to zero.*/
    static final String FCB1010_BANK_LIST[] = new String[] {
        "FCB1010 Patch"
    };
    /** List of patch numbers for driver.
        * The FCB1010 does not use patch numbers, so this is set to zero.*/
    static final String FCB1010_PATCH_LIST[] = new String[] {
        "FCB1010 Patch"
    };
    
    /** Converter Match ID--Used to match a patch to a FCB1010Converter*/
    static final String CONV_SYSEX_MATCH_ID = "F0 00 20 32 ** 0C 0F";
    
    /** Dump Request ID--Sent to FCB1010 for a dump request.*/
    static final String FCB1010_DUMP_REQ_ID = "F0 00 20 32 *channel* 0C 0F F7";
    
    /** Patch Type String*/
    static final String FCB1010_PATCH_TYP_STR = "Settings Patch";
    /** Sysex Match ID--Used to match a patch to an FCB1010SysInfoDriver*/
    //    static final String FCB1010_SYSEX_MATCH_ID = "F000000E13**00";  Not used. Sys Info patch sometimes has wrong opcode.
    static final String FCB1010_SYSEX_MATCH_ID = "F0002032**0C0F";
    /** Dump Header Bytes--Bytes in a system info dump header*/
    static final byte[] FCB1010_DUMP_HDR_BYTES = {
        (byte)0xF0, (byte)0x00, (byte)0x20, (byte)0x32, (byte)0x01, (byte)0x0C, (byte)0x0F 
    };
    
    /** Sysex program dump byte array representing a new patch*/
    static final byte[] NEW_SYSEX = {
        (byte)0xF0, (byte)0x00, (byte)0x20, (byte)0x32, (byte)0x01, (byte)0x0C, (byte)0x0F, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3E, (byte)0x00,
        (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x01,
        (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7A, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x05, (byte)0x0A,
        (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x68, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x17, (byte)0x00,
        (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x03, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x5F, (byte)0x00,
        (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00,
        (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7D, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x02, (byte)0x00,
        (byte)0x01, (byte)0x00, (byte)0x05, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x74, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x0B, (byte)0x7F,
        (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x06, (byte)0x00, (byte)0x50, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x2F, (byte)0x07,
        (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x40, (byte)0x07,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3E, (byte)0x00,
        (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x01,
        (byte)0x00, (byte)0x08, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7A, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x05, (byte)0x0A,
        (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x09, (byte)0x00, (byte)0x00, (byte)0x68, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x17, (byte)0x00,
        (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x0A, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x5F, (byte)0x00,
        (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00,
        (byte)0x0B, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7D, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x02, (byte)0x00,
        (byte)0x01, (byte)0x00, (byte)0x0C, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x74, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x0B, (byte)0x7F,
        (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x0D, (byte)0x00, (byte)0x50, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x2F, (byte)0x07,
        (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x40, (byte)0x0E,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3E, (byte)0x00,
        (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x01,
        (byte)0x00, (byte)0x0F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7A, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x05, (byte)0x0A,
        (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x68, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0F, (byte)0x17, (byte)0x01,
        (byte)0x7F, (byte)0x0B, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x11, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x5F, (byte)0x00,
        (byte)0x0F, (byte)0x01, (byte)0x7F, (byte)0x0B, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00,
        (byte)0x12, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7D, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x0F, (byte)0x01, (byte)0x7F, (byte)0x0B, (byte)0x02, (byte)0x00,
        (byte)0x01, (byte)0x00, (byte)0x13, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x74, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x0B, (byte)0x7F,
        (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x14, (byte)0x00, (byte)0x50, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x2F, (byte)0x07,
        (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x40, (byte)0x15,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3E, (byte)0x00,
        (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x01,
        (byte)0x00, (byte)0x16, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7A, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x05, (byte)0x0A,
        (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x17, (byte)0x00, (byte)0x00, (byte)0x68, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x17, (byte)0x00,
        (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x18, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x5F, (byte)0x00,
        (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00,
        (byte)0x19, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7D, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x02, (byte)0x00,
        (byte)0x01, (byte)0x00, (byte)0x1A, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x74, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x0B, (byte)0x7F,
        (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x1B, (byte)0x00, (byte)0x50, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x2F, (byte)0x07,
        (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x40, (byte)0x1C,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3E, (byte)0x00,
        (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x01,
        (byte)0x00, (byte)0x1D, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7A, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x05, (byte)0x0A,
        (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x1E, (byte)0x00, (byte)0x00, (byte)0x68, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x17, (byte)0x00,
        (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x1F, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x5F, (byte)0x00,
        (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00,
        (byte)0x20, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7D, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x02, (byte)0x00,
        (byte)0x01, (byte)0x00, (byte)0x21, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x74, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x0B, (byte)0x7F,
        (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x22, (byte)0x00, (byte)0x50, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x2F, (byte)0x07,
        (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x40, (byte)0x23,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3E, (byte)0x00,
        (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x01,
        (byte)0x00, (byte)0x24, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7A, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x05, (byte)0x0A,
        (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x25, (byte)0x00, (byte)0x00, (byte)0x68, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x17, (byte)0x00,
        (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x26, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x5F, (byte)0x00,
        (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00,
        (byte)0x27, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7D, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x02, (byte)0x00,
        (byte)0x01, (byte)0x00, (byte)0x28, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x74, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x0B, (byte)0x7F,
        (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x29, (byte)0x00, (byte)0x50, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x2F, (byte)0x07,
        (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x40, (byte)0x2A,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3E, (byte)0x00,
        (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x01,
        (byte)0x00, (byte)0x2B, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7A, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x05, (byte)0x0A,
        (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x2C, (byte)0x00, (byte)0x00, (byte)0x68, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x17, (byte)0x00,
        (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x2D, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x5F, (byte)0x00,
        (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00,
        (byte)0x2E, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7D, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x02, (byte)0x00,
        (byte)0x01, (byte)0x00, (byte)0x2F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x74, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x0B, (byte)0x7F,
        (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x30, (byte)0x00, (byte)0x50, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x2F, (byte)0x07,
        (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x40, (byte)0x31,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3E, (byte)0x00,
        (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x0A, (byte)0x00, (byte)0x01, (byte)0x01,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7A, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x05, (byte)0x1B,
        (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x68, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x17, (byte)0x00,
        (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x02, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x5F, (byte)0x00,
        (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x00,
        (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7D, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x02, (byte)0x01,
        (byte)0x7F, (byte)0x00, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x74, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x0B, (byte)0x7F,
        (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x05, (byte)0x00, (byte)0x50, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x2F, (byte)0x07,
        (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x40, (byte)0x06,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3E, (byte)0x00,
        (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x01, (byte)0x7F,
        (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7A, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x05, (byte)0x1B,
        (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x08, (byte)0x00, (byte)0x00, (byte)0x68, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x17, (byte)0x00,
        (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x09, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x5F, (byte)0x00,
        (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x00,
        (byte)0x0A, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7D, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x02, (byte)0x01,
        (byte)0x7F, (byte)0x00, (byte)0x0B, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x74, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x0B, (byte)0x7F,
        (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x0C, (byte)0x00, (byte)0x50, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x2F, (byte)0x07,
        (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x40, (byte)0x0D,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3E, (byte)0x00,
        (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x01, (byte)0x7F,
        (byte)0x00, (byte)0x0E, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7A, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x05, (byte)0x1B,
        (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x0F, (byte)0x00, (byte)0x00, (byte)0x68, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x17, (byte)0x00,
        (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x10, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x5F, (byte)0x00,
        (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x00,
        (byte)0x11, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7D, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x02, (byte)0x01,
        (byte)0x7F, (byte)0x00, (byte)0x12, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x74, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x0B, (byte)0x7F,
        (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x13, (byte)0x00, (byte)0x50, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x2F, (byte)0x07,
        (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x40, (byte)0x14,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3E, (byte)0x00,
        (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x01, (byte)0x7F,
        (byte)0x00, (byte)0x15, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7A, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x05, (byte)0x1B,
        (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x16, (byte)0x00, (byte)0x00, (byte)0x68, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x17, (byte)0x00,
        (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x17, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x5F, (byte)0x00,
        (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x00,
        (byte)0x18, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7D, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x02, (byte)0x01,
        (byte)0x7F, (byte)0x00, (byte)0x19, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x74, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x0B, (byte)0x7F,
        (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x1A, (byte)0x00, (byte)0x50, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x2F, (byte)0x07,
        (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x40, (byte)0x1B,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3E, (byte)0x00,
        (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x01, (byte)0x7F,
        (byte)0x00, (byte)0x1C, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7A, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x05, (byte)0x1B,
        (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x1D, (byte)0x00, (byte)0x00, (byte)0x68, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x17, (byte)0x00,
        (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x1E, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x5F, (byte)0x00,
        (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x00,
        (byte)0x1F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7D, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x02, (byte)0x01,
        (byte)0x7F, (byte)0x00, (byte)0x20, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x74, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x0B, (byte)0x7F,
        (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x21, (byte)0x00, (byte)0x50, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x2F, (byte)0x07,
        (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x40, (byte)0x22,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3E, (byte)0x00,
        (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x01, (byte)0x7F,
        (byte)0x00, (byte)0x23, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7A, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x05, (byte)0x1B,
        (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x24, (byte)0x00, (byte)0x00, (byte)0x68, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x17, (byte)0x00,
        (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x25, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x5F, (byte)0x00,
        (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x00,
        (byte)0x26, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7D, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x02, (byte)0x01,
        (byte)0x7F, (byte)0x00, (byte)0x27, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x74, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x0B, (byte)0x7F,
        (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x28, (byte)0x00, (byte)0x50, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x2F, (byte)0x07,
        (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x40, (byte)0x29,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3E, (byte)0x00,
        (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x01, (byte)0x7F,
        (byte)0x00, (byte)0x2A, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7A, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x05, (byte)0x1B,
        (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x2B, (byte)0x00, (byte)0x00, (byte)0x68, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x17, (byte)0x00,
        (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x2C, (byte)0x20, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x5F, (byte)0x00,
        (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x00,
        (byte)0x2D, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7D, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x02, (byte)0x01,
        (byte)0x7F, (byte)0x00, (byte)0x2E, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x74, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x0B, (byte)0x7F,
        (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x2F, (byte)0x00, (byte)0x50, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x2F, (byte)0x07,
        (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x40, (byte)0x30,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x3E, (byte)0x00,
        (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x1B, (byte)0x01, (byte)0x01, (byte)0x7F,
        (byte)0x00, (byte)0x31, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x7A, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00, (byte)0x7F, (byte)0x05, (byte)0x1B,
        (byte)0x01, (byte)0x7F, (byte)0x00, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x78, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F,
        (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x7F, (byte)0x78, (byte)0x7F,
        (byte)0x7F, (byte)0x40, (byte)0x14, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x13,
        (byte)0x6E, (byte)0x15, (byte)0x6D, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0A, (byte)0xF7
    };
}