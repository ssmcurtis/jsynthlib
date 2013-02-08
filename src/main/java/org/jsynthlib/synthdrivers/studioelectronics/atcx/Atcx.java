package org.jsynthlib.synthdrivers.studioelectronics.atcx;

import org.jsynthlib.model.patch.PatchDataImpl;
import org.jsynthlib.tools.DriverUtil;
import org.jsynthlib.tools.HexaUtil;

public class Atcx {

	public static final String VENDOR = "Studio Electronics";
	public static final String DEVICE = "ATX-C";
	
	public static final int HEADER_SIZE = 9;
	public static final int PROGRAM_SIZE_SYSEX = 74;
	public static final int PROGRAM_SIZE = PROGRAM_SIZE_SYSEX - HEADER_SIZE - 1;
	public static final int PROGRAM_COUNT_IN_BANK = 128;
	public static final int PROGRAM_COUNT_IN_SYNTH = PROGRAM_COUNT_IN_BANK * 4;
	public static final int BANK_SIZE_SYSEX = HEADER_SIZE + (PROGRAM_SIZE * PROGRAM_COUNT_IN_BANK) + 1;

	public static final String[] BANK_NAMES_PATCHES = new String[] { "Bank 1", "Bank 2", "Bank 3", "Bank 4" };

	public static final String DEVICE_SYSEX_ID = "F000004D02";
	
	public static final byte[] DEFAULT_BANK_HEADER = new byte[] { (byte) 0xF0, 0x00, 0x00, 0x4D, 0x02, 0x00, 0x00, 0x00, 0x00 };
	public static final byte[] DEFAULT_PATCH_HEADER = new byte[] { (byte) 0xF0, 0x00, 0x00, 0x4D, 0x02, 0x00, 0x00, 0x00, 0x01 };

	public static final String DEFAULT_PROGRAM_STRING = "032000000005000700000C422F4C457F4A00004C6400150049000000007400002E000000012A0000000102000100010B0E060040004000030070005D01590000";
	public static final String DEFAULT_BANK_STRING = "F000004D02000000002516000000010D0A00003F231E611A7E002E002252581A5B6A5513141C4213283A00002B010000000000020001000107180B0640014000000070175D01590000041700000000010900003F401E611B79002E00175C581A246A5514011C4213002E00000101000000000002000100010B080D1948004000000070005D01590000051D00000004290900003D441E611B7E002E001759581A2E3A0014031C4213005E00000009000000000002010100010F140D0040004000000070005D01590000031A00000013000905000C400D1B7C515600003E694A28003E001100356D110041000001017F1600010102020000010B120D003C000000000000005D01590000081F00000000000900000C432B504A6F230000173A1A31034D2D0000773E15002E00000001460031000102020100010B0E0D0040000000000070195D015900000714000000040A09000000421E61137F1000002732571A2E3A0025031C4220002E00000001000000000002000100010B0F0D0040004000000070005D015900000D14000000190009000000402F236E7B7200002B4640202F777F00007F0100002E000000016B0000000102000001010E200B0840000000000070005D01590000081F000000000009000014432B500A6F23000024411A23034C2D0001773E28002E00000001460031000102020100010B0E0D0040010000000070195D0159000008170000000503090000193E6F1B357F13000015541A373C56721700357F17002E000001011E0031000102000101010E150B1A40000000000070005D015900000314000000010502000008403523317D4900001A3631040E3B0015017C0E17005800002608000077000102040100010B0E0600520300000300700C5D01590000031400000001050200000840352331692D0000253931040E3B0016017E0E17005800002608000077000002040100010B0E060052030000000070005D015900000221000000010502000000403423756B620000244830120E3B000201740E10002E00000001170500000102040100010B10060052000000000070115D01590000281700010007000200000C403224005A5F00001F561A343A552D0100305119007F00001A0D65007800000200030101074C010040030000000070235D01590000281700010007000200000C403224005A7E0000112C1A1A7F7B710100303F19007F00001A0D65007800000200030101074C010040030000000070235D01590000031F000000130009050000373500055C770000001E1D465C74771303184915005500000202000000010102000101010B0E0D0030010000000070005D015900000B1F000000030809000000423A5C0B6B3E0500244247112C7B7F1900184818002E00000101000000010002000301010B120D00400340000000001C5D015900000B1C00000003440C0000004060416E717F00001B4C40002F777F00007F013C005D0F000A0728007F010002000101010B0F012F40000000000070005D01590000041F0000001300090500183F7F285A791E00002A23373E422F7F19007F0015002E000001016B0000010102000101010B110D0047000000000070035D01590000181F000000130009050018403B40735D00001739253F00003F2A00007D400000530B000007000000000102000100010B0E0D0040000000000000005D01590000181500000013000905000F403B3B1A5812000430484C00000B380000007F0A00530B0000070000000001020201000107140D0040010000000000005D01590000171F00000013000905000F403C3C191C7200040056497F000B383900007F3A00530B000007000000000102040100010B0E0D0040010000000000005D01590000091F000000142A2A000014403C3C1A3E7200012341494E000238000015760000530700480700005A00010200030001052D0D0040010000000001055D015900000614040C00130009050000460D1B757F7F00002455451822427B1800356214002E000000017F017F010102020100010B7F0B0F40000000000070165D01590000031A00000013000905000C400D1B6E7E49001528324A4026007F1100356D0A0041000001017F1600010102020101010B120D003C000000000000005D01590000031A00000013000905000C360D1B6E7E490000282E4A4026007F1100356D0A002E00000101000000010102000101010B120D003C000000000070005D015900004F1F000000130009050013400D1B35623600001E4D59632D007F1D00356212006800002D0E7F017F0000020003010107150B2A40000000000000005D01590000520E0901000815090000003200014039360000055816254F7E004D00007F7F007F0000050A00003700010200010001106E000040000000000070005D015900005909780705050609000000347F3E612D4F001F4E611F2C0000135E0000517F002E000000017F017F000002030100010A200D0056004000000001005D01590000001C00000008040700000040342375587F0000405C162E0043000000007F0000220F00250E00007F000002000100010B0D020040000000000070005D01590000020639040106453803000038413A4B7E000034304B1601003761000019506518520000150500027F0000000801000105300B0951004000000001005D01590000211500000000000900000C443823687F6300001B002D001401001C0028571D0034000000013F017F00000C160100010F350B0D40000000000070005D015900001B0B7F0703057F02000017404C002A37374A001B5B312C0C73001C00285774125D002D32084D007F000000180001010A0E0D0040000000000070005D01590000021700000013000905000C3D0D26585F1B00003B2E121B05320015006F0F17002E000000017F017F000102000100010B0F0D0033000000000070005D015900000006080800050C0900000036343B53507F00003D5D162C0000132200006223002E000000017F017F000002000100010E350D0040004000000070005D01590000070224090001070900001250347F007D5600002B151C0F000006220000623C416600001A087F017F000002000100010E350D0040014000000070005D0159000000200001001300090500004127595A767200001546593042017F1300356418002E00000101000000010002000001010B0D0D007F000000000070005D01590000131A000000000009000000400035756B7A0000236257192B7F790A176C3B0A002E000001010A0031000002000101010B0E010040000000000070005D0159000005153F0600072B07010015400D6F15791E00002562291B00757511002D2F6D016600006E0400007F000002000100010B6E0D003D030000000001005D01590000081F0000000A0C080000003F0D3C167F570000360F3A2A66547F1900355415002E000001016B0000010002000301010D51010040000000000070005D01590000081F00000005000900000C4034236B6C690000285B16371E4A7F1600007F14002E00000001000000010102040101010E210D2F40000000000070005D01590000001D000100020B0C00000C3F7F031378270D124B537F1D51267F00003B3C05003300000101717300010002000101010B0A0D004E034000000000135D01590000001F00000013000905000C3D040C1B7A27000043437F1D4C267F00003B3C00002E0000010171730001000018010101042B0D004E006000000070005D01590000001D0001000300040000134374075B7F1302003A4D152D034D000000700025042000001306717300000002000000010B0B0D007F006000000070005D0159000007170000000503090000193E6F1B357F1300004B541A371A70091708356217002E00000101372331000102000101010E410B1A40000000000070005D0159000002087A0302050E090000003A342353005A00007A5D162C000013512200767F002E000000017F017F0000020001000104150D0032004000000070005D01590000090353040005120900001332211B317F000000223F3402014900541D497B60065100000E0878000001000200010001107F0D0040010000000070005D015900004E04160200135A0005000040771B327D5C004D6E57184A007300191935412D005D00002D0E00093C000002000301010B7F0D0037000000000001005D015900000409000B000E7F1B0100173D3A39087E6C136D347E5D0E00137F1337280B7A007F000018065A0079010002030100010B14010040016000000001075D0159000004090E0900197F1B0100103D3539087E6C156D4E635D0E00131713253B097A007F00000B055A0079010002030100010658010040016000000101075D0159000004094309000E7F1B0200083D2F39087E6C136D4E515D130E64000F343B1D7A007F0B000B055A007901000718010001075E0100400160000001010A5D01590000060301020008010100000C37003575157F000D5B4E28192B7F792517720026031D02005C030A0031000000180101010E730B0040000000000070005D01590000001F0000000811020000063B34232E7F42000029002042254F7F0C007B00190215000038064B0A7F01000200000101012A01007F010000000070005D01590000010D0007000500090000003D1E45533A7F000F505C3E36194A7F0000792300031D00007F047F0000000002030100010B0B010040006000000070005D015900000213000700010D020000123F3432267F280B0047601611004706250000692D002E000000017F017F000102000100010B0E010036014000000070005D01590000111300000013000905000C3E2C37526C7500006457552A69497F1900356215002E000001016B000001000200010101042F0B0740004000000070005D01590000001F00000000040A00000C404645567D34000C49331628324A7F0E00007F00002401007F03000000000002000101010B0D094B40000000000070005D01590000010D000700050009000000362D2870307F000F0F603E364D767F0000792312001D00007F047F0000000002000101010B0D010040006000000070005D01590000021300000013000905000C3F2C37526C7500001A52552A69497F1900356221002F130002016B007F010002000001010E2C0B0740000000000070005D01590000041600000013000905000C360D1B6E7E4900001C3B4A1F4C167F1100356D12003A00004007000000010102000100010B0A0D003B000000000070005D0159000008091A0A0209210705003F4D1E611B69002E000B58581A2A6A5514011C4213005A19006308000000000002000100010C270D1948004000000070005D01590000060400000004000900003E447F611977003A00185C581A367F001401360013002E000004010000000000020001000104150D0040004000000070005D01590000070513090014120A00003F461E611B78002E00175C581A0B6A4714011C421305260000210800000000000200010001040A0D1948004000000070005D015900007C09360A0200280200003F4738441B79002E00175C581A2A6A5515001C4251005B19006308000000000002000100010B2D0D1936004000000070005D015900000F1A0000000000090018303F223570591B000030313A00263555142C446B19002E000001012D0031000102000100020B0B0D0040002000000070005D015900000C1B00000000000900181440483523541E00003C502C072635551E286A621E002E000001012D0031000102000100020B0E0D0040012000000070005D015900000E190001000703020018003A387F6344450000362734270017001D18651E1D002E000048057F3269000102000100010B0A0D0040002000000070005D01590000181C000000190009003C30400F4250134D420F32532C1B08541A0F00787F09000000000103290000000002000100010E120B0640000000000070005D0159000001170001001100030024397D7F12002139000F7E2B7F00000C59211C7F7F2200260078060B7F51560000025833007F0B0B0D1F4003200000000000000C58660001170001000F1B0300183C400D56763B1B000028002C701D3B6F2C122F76660A121D120001244B47000002000100010B0A0D3B40004000000070005D01590000031E00000011000300243C400D5676471E00002D002C701D3B6F1E122F56210A121D120001241447000002000100010B0E0D3B40004000000070005D01590000011E000000110003002430400D56764740000028002C701D3B6F1E122F2A1E0A121D120001246400000102000100010B0D0D3B40004000000070005D015900000425000000130009052430400D5676620D000030002C701D3B6F1E194C351D0A121D1200013A7500000002000100010B0E0D0039004000000070005D01590000011F0000000204090018253F71251B483200003658493042017F0011356200002E000001017F0000000102000101010B0E0D0022000000000070005D015900001314000100040009392438407F0000272D00127F4C7E7E000C5C501C7F7F6900000C786107000A000000025833007F0B0500004003200000000000000C58660000140000001300090524003F0D5636415100002C192C701D336F1C12475B1C0A121D0001017F3E00000002000100010B0C0D00400040000000700C5D0159000008150000000F25040018244034365647210000333F5300286A55111D6A6217002E00000101001A00000102000100010B080D0040000000000070045D01590000120E0007000E7F040018184034363662000000354C5300286A55111F6A6257002E00000101000000000102000100010B120D0040030000000070005D01590000051400010004000939243E7F740000467900122F2F7F00000C59211C7F7F4D00000C7861077F7F7F0000020031007F0B0600004001200000000000000C586600091B0000000B1C0800242F4066321B612C00003F344300286A55131F6A6215002E00000101000000000102000100020B080D0040010000000070005D015900000D14000100071203002424407F0040133B00007F504E7E000C30051A7F7F0200000C785E0700007F000002003300010B1100004000200000000006000C58667F000A020100115D0400183C3F0D1B76622E000D5C205170140236360D3562690007000073067F247F0000020000000104430D0040004000000070005D0159000000260000001300090518303F0D1B76622E000D2B434470141936180D35621F0007000073067F017F010002030000010B160D0040000000000070005D01590000072E0000000105090018183C2A1B703B4900001252497014006F190035622E26460000000167177F000002000100010B140D0040000000000070005D01590000022F00000004250A0018243D32315B413B0008124D497014006F32003562002646000000010C017F000002000100010B140D0040000000000001005D015900005B0E0004000009050018336F427D0B134E1D136B5A2F45007159241C7F7F1C00000C7800037F7F7F000102003C3A7F0B3503014059600000000000000C586600001A00000008080700242740473500272700004D321A0E19660019076F0015002E0000060B007B7F000002080001010B080D1D40000000000070005D01590000011F00000000320A000C1E40342D1C115300002D3F3C12584A7F0000165800002E00002503000000000002040101020B0E0D2F49000000000070005D01590000021600000007070700001840732318037C00002D3F431257207F0B0016580C24603E001603016300000102000101020B080D2F49010000000070005D015900000513000000122108003C24400F2450484D000F354F2C1A09511A0B00787F0F00000000010329000000000202030001103A020040000000000070005D01590000001F00000013270C001800400B3352471E0000674D211625297F0A111B0F00001F23005D034C697F01000200010101081E010040000000000070005D01590000021B00000013000400182E3B35632E5F0034003E342C350004000E00007F0E004E00002402000000010002000101010B0B010040034000000070005D01590000001900010001140200182F3B35632E56001E1445394E350059000E00007F00004E00002402000000010002000001010B07010040034000000001005D015900000019000000123A030024363B35632E41001E1F553A4E350059000E21007F11004E00002402000000010002000001010B07010040034000000001005D01590000001B0000001300040024313B35632E4B00220345344E350004000E00007F00004E00002402000000010002000001010B08010040034000000001005D01590000111B0000000500090018294000330A462700003E43260B2A69541527665F15002E000001012D0031000102000100020B0A0D0040012000000070005D01590000151200010012230300183365427D0B003E000E19582F45007159221C7F7F22001A00787F037F7F560001020233007F104B0B074059200000000000000C58660036041F0700130009050C24384B56441D1400001D5B5B66006722650A6335740064005C540A00000000010200010001101F0D0040000000000070005D0159000036041F0700130009052424384B5646710000003D5B5B66006722650A6335740064005C540A000000000102000100010B0E0D0040000000000070005D0159000041041E07001300090524243C4B5676754400006E570266006722650A6335741636085C260A3A7500000102000100010B2C0D0040004000000070005D015900002D031207001300090524303D4B5671165400002B585B66006722650A633574425E005C130A320000000102000100010B1B0D0040004000000070005D0159000033091209000001020024303D455672302800001F595B66217C22650A633A742651005C080B3A0A000101020001000110130D0040004000000070005D015900001F041E070005240B00241540413F621F1700001C505B66137522650A633574003F005C410A00000000010200010001103B0D0040000000000070005D0159000049030507001311090024163C403D5B553600002E615B080A0022650A6335742636085C000B3A750001010200010001105E0D0040004000000070005D015900003F090907000A11090024153C40314B552900002E615B080A0022650A6335742636085C000B3A75000101020301000107100D0040004000000070005D01590000350B1B07050B230805241344403140453E0C002F615B080A0022650A63357F2636085C000B3A7500010102000100010B510D0040004000000070005D015900002B001B07000702020024124440314055410C002F615B080A0022650A63357F2636085C000B3A750001010200010001107B0D0040004000000070005D0159000021041B07010702020000002B403140493E0C002F615B080A0022650A63357F2636085C000B3A7500010102000100010B7F0D0040004000000070005D01590000011400030108440B0024367F7F00000C580E0A5B6D7F4C000C59791C7F7F7E00000C7861077F7F7F000000582300010B7F00004003400000000000000C5866000A0A3D0001167F03010C0E2B34231C3453187F277F3C12004A7F7F001E467F002E00000001000000000100030101020A1D0D2F49034000000070005D01590000021F0000000D5F02000C0E4034231C11531800147F3C12004A7F001F00437F002E000000010000000001060001000201330D2F49014000000070005D01590000000F11020209700600002D7F027F24280057007F677F4C0000764D0977005500006A007F01692700017F007F027F000E1E01047F016000000000005D055B02003A03561901097F1A010004340000524B4B00003E54160B236100001A79373A1F0E0000270D000000000002000100020A7F077F34004000000070005D015900003803560901037F0A0100054A0000495E5D1F0024681E0B236100005179276F1F0E0000270D00000000000506010002075E0D3F34006000000070005D01590000000A0900000E55090100092D0D2F4A36351348311E3A30546929507F00667F00110018350137000000000200000001106F003A7F006000000070005D015900000B001C00010B07020018183C1C3956463200002E567F00005100490146586730000000190A00000000000012030001093F041140004000000024004E046000007C197F0703046400025C1F3F027F74545A2A007F337F4C00007600097F000000006A000001006600007F007F037F00057F050019010000000000005D055B02000200390700194C0A000012403423167F37000534481825004A7F16181E00000F1B000024070073000100020001000101260D2F4C000000000070005D015900000B03000C03030005000007530038766B4A221B2F492B090E2B000F22211214095D142D7F0D432E7F010002000101010B7E0D0040004000000070005D0159000026080001000B0006000007494C3876511B00134F122B090E2B001C0034071D125D002D00083F017F00000010010001010A0D003C004000000070005D01590000030E751300000006020000530038764F2500115E082B090E2B000F1F21127F004C002D05063F017F010003000101010D460D00280040000000701D5D0159000008097A0400011300020217415C5973531B001623475932010118600035626025500000300700000001000018010001037E0D7F25004000000070005D015900001508000100282B06000D00494C383E000000002C322B540331001C003C001D1015002D7C06776F7A01000200030001057F0D00400140000000016A5D0159000015080001000F2B06000D25494C383B350000002C322B540331001C1B3C0D1D1015002D7C0677787A0000020C030001057F0D00400140000000016A5D0159000015080001000B001C00000C2071386E000D007F2C2F2B551535001C043E001D0019002D4D0300007F010000180100010D7F0D0040004000000000005D01590000000A0B09001900090001114034005233290000002D166959007F2E00007F7F004E0000400B00000000010200000001073708347F000000000070005D01590000021F000000194C0A000018403423167F3F00052C481825004A7F162A1D0000001B0000220700730001000018010001043E0D2F4C002000000070005D015900005B0E200805030D00020001403423767B6200172F4919251E007F1A00007F7F7B7F7F000C05000000000100090100010B190D2F2D000000000070005D01590000051F0000000A4A0A000C0F40342D1C135600004B4C3C12584A7F6A0016587B002E0000000100000000000200010101047F0D2F49032000000070005D01590000F7";
	public static final String DEFAULT_BANK_STRING2 = "F000004D02000000000B1A0000000000090000004000357561700000156257192B7F790A176C3B0A002E000001010A0031000002000101010B0B010040000000010070005D01590000001F00000009020100000038350A42794000001767197E00270016001B5200002E00000101000000010002060000010B0E01007F000000010070005D01590000001F000000090201000018374E24327D370000125F197F297F7F13001B5200002E00000101000000000102000001010B0E01007F030000010070005D01590000071D0000001900090000004034236C7E2700001C4F2436244D7F0000720100002E00000001000000000002030101010B1A012F40000000010070005D01590000051D00000019000900000C4034236E7E460000274000361E4A7F0000720100002E00000001000000000002030101010B1A012F40000000010070005D01590000251D00000006110200000B4034232C7F7200004D40097F0D607F00051B6D00002E00000001690000000002030101010B1A012F40010000010070005D01590000041A35080013150701000C3735000C6B6300000063133F6B7B7100015B2200002E0000010100000000010200010101000E010052030000010170005D0159000000160001000B00020000003D4C3832744A4A002C2D22001401001C0028571D125D002D32083F017F000002000000010B0E0100400300000100700C5D015900000414000000020F09000018404300685A180000123E5816476A5514016F0713002E00000101000000000102000100010B0D010048004000010070005D015900003A041E0700137B0B02000840413F405A540001276E5B00007522650A634D740000005C000A000000000102000100010A0300202C000000010101005D015900006704480705135B0B010000331640426B470026287A5B0013007F650A633B7D0000005C2807000000000102000100010C4B07111E000000010101005D015900000B04400700137F0B02000C386C55526741003924765B0013007F651E2A577D0026005C7A0400000000010200010001107F0E1340000000010070005D015900000D043C0700137F0B01050B3D75335261600048264C5B0013007F650222577D00005E5C2806000000000102000100011045027F40000000010170005D015900001B043C0700137F0B01050C3875535275600044266B5B0013007F650222577D00005E5C2806000000000102000100010C35027F40000000010070005D0159000036041E0700135B0B01000C406D6340713C000026772316242F0065001B407D0000005C7F0400000000010200010001060F0D0040000000010070005D0159000021001E07020D5B0B02000C407F5F40713800001B77230500570065001B407D0000005C7F0400000000010200010001061F0D00400000000100702C5D01590000071F00000003000A0000003C00360E7F51000013511A052D7F4E1300315916002E00000101000000000002000101010E23010040010000010070005D015900000B1F00000003110A0000013C002806586900001C421A20167F4E1B0031591B002E00000101000000000002000101010E23010040010000010070005D01590000041600000003110A00000C3C002856607F0000163F1A25367F4E1500315920002E0000010100000000010200010101000E0B1540000000010070005D01590000001F00000008660801000C407F1B23381C00022D1B20303D6C01003F7A010076007F00410200000001000200010201010D000040030000010001005D01590000001D00000003170200000E400E233A7F3D005007290029354F7F00007B1A120215000038064B0A7F010002000001010B0E01007F030000010070005D01590000001D00000003170200001C4034232E7F3D0000073A2040354F6E00007B00120215000038064B0A7F010002000001010B0E01007F030000010070005D01590000000C5A0400020B02000014401C232A520000001E712D1B354F7F00061F00000215000038067F0A7F01000200000101000E01007F010000010070005D0159000000000907000811020000133B34232C4636000029702042254F2200007B00120215000038064B0A7F010002000001010E3301007F010000010070005D01590000001F0000000811020000063B34232E7F42000029712042254F7F17007B00280215000038064B0A7F01000200000101010E01007F010000010070005D01590000001B020200090201000006397F2434645A000F1E274D59300075162235620040740000270200000001000200000101054001117F010000010070005D01590000001F0000000811020000063B34232E7F42000029002042254F7F0C007B00190215000038064B0A7F01000200000101010E01007F010000010070005D015900001D050207000A06090000003761282A7D0000000F392C1C0B30001600244325001F000053046B007F010002000100010B29010040010000010070005D015900000A1F0000000103090000153753470A6900000019392C0800302D1600356225003500005E0422007E010102000100010B0E010040010000010070125D01590000071F00000013000905000C420D58067F000000005B15405B2F7F16003562152E51430058046B0000010102000103010B0E010040030000010070005D01590000071F0000001E1D0A050001420D4A067F000000006321402A397F11003562132E51430058042D4600000002000103010B0E010040010000010070005D01590000031B00000013000905000E364715287D4700000C451C16001A0A0000274711002E00000001000100000102050100010E360B0C40030000010070005D01590000030A020200130009050000440D155E7F4700001D6C45791A270000007A00110625000019067F014B00000200010001092D010040004000010070005D01590000030A01020013000905000744326C527F5B0000115B49191E352900062257120625000019067D014B01000200010001092D010040004000010070005D01590000020C0702000901010000054434231B7F410000325D163A0000000005285F13003E00002D057F01640000020001000104410B322A014000010070005D01590000040A0000001400020000094027310475470000115D080772017F180021622D001800005D04130900000102030101010E120B0040030000010070005D01590000110A00000014000200001F402731046D440000115D080772017F1800216235001800005D04130900000102030101010E120B0040030000010070155D01590000031300000013000905000C3B7F45537F5000000E006430267A231D007E0016002E00000101000031010102000101010B0001004D000000010070005D01590000001D000000030F0A0000003971595A006E0000185F307F284B7F0000206200002718007F037F0A00010002000101010E4B0B0C40004000010070005D01590000021F0000000500090000004034236B007F001A1F2316371E4A7F0000007F0E002E00000001000000000002000101010B0E000052000000010070005D01590000071C0000000A1307000015402C2A0E7F00001006672E417F137F0F00355712285600007F05190032000002000101010B0C010040014000010001005D015900000A1F00000004010500001A402C2A0E7E00000E07752E417F637F0F00273712285600007F05190032000002180101010B0A010040014000010001005D01590000052C7E03000C190200000E402C2A0E0042000C14752E417F6D18000027327F286C00007F05190032000002000101010550010040014000010070005D015900006A03310A01097B09010015402C411264000000005E27417F637F0F2E35577F466800007F05190032000002000101010B0A010000014000010070005D01590000021300000013000905000640771B367F43000011577F0F267A231D017E0016002E00000101000031000102000101010B0001004D010000010070005D01590000021F00000013000905001140771B367F43000016577F30267A231D197E0016002E0000010100003100010200010101061A010051000000010070005D01590000520E0901000815090000003200014039360000055816254F7E004D00007F7F002E0000000100000000010200010001000E000040000000010070005D01590000092A00000019000900000048345F087F2100000A3C1E37196B7F0000007F00002E00000001000000010002000101010B0E000040030000010070005D015900000B2A000000190009000000483400267F2800002747192E00442A0000007F00002E00000001000000000102000100010B0E000040000000010070075D01590000021C00000002090900002448440022691100002247192E00442A0A00007F0F002E00000001000000000002000100010B0E00002D010000010070045D015900000218000000190009000014480000227D420005156C1C2E00442A0A0B1C0C0F002E000000017F0000010002000100010E0C000040000000010001005D01590000001A000000010E090000143A34230469693000005D2D1A0000131C0000621C002E0000000119017F000102000101010E2E0B1140032000010070005D01590000061A000000010E090000003A20650069690000005D2D1A0000131C0000621C00340000000019017F000102000101010F2E0B1149032000010070005D01590000191A000000130009050000400D1B747F5A000017481912233A7F1300356214002E000000017F017F000002000101010B140E0040000000010070005D01590000071A000000010E090000063A34230E69693000004B00230000131C0000621C002E0000000119017F000102000101010E2E0B1139012000010070005D015900000312000000010E090000063A34551E697830002B4B00230000131E0000621D002E0000000119017F000102000101010E2E0B0B36012000010070005D01590000001F0000001105010000003C347F567D3000001B11003A00652A05002F7305003900005B07000000000002000100010B0E00007F000000010070005D01590000001C00030007770A0100153B236119006E0000395B000A0024000E002F736811311B00300700000000000200010001011800007F030000010070005D015900002A0D02010009030200000C400A75527D7C000017150B0500140000152A7F37002E00000001000000000002000100010B0E010040000000010070005D01590000250D020100090302000000401C464078650000164A0B0500140000152A7F37002E00000001000000000002000100010B0E012F40000000010070005D015900000E0A010100025702000000401C2A00696900001937160500140000152A7F37002E00000001000000000002000100010B0E012F40010000010070005D01590000071F0000001300090500003F0D282C7E28000002672C40392F7F16211C0015002E000001016B0000010102000101010E1D0B0040010000010070005D01590000041F0000000A08070000003F0D282C7E26000015750028002F7F1613210915002E000001017F0000010102000100010E1D0B0040010000010070005D01590000041F00000013110A0500003F0D594D7F260007036F2C407F2F7F16161C004B002E000001016B0000010102000101010E1D0B0040000000010101005D01590000021F00000013000905000C3F617F667F0000111966325700074116002A0015001C00003004320000010002000100010B0E0B002A000000010070005D01590000430C020100070402001818402535765136220001355830263555312C6A6236002E000001012D003100010200010001027F010040002000010070005D01590000291A0000000A1808001826400D56367A0000000B391B45002A40201E2C511D0A121D120001000000000002000100010B0700003F004000010070005D015900002E1C0000000A210800181F400D56367A120000004A2A45024A40201E2C511D0A121D120001000000000102000100010B0700003F014000010070005D0159000029000904000A330300242C3C0D56664E1F00002F671B1B00003B40212C516A03170012430600000001010200010000041300003F024000010070005D01590000021300020004310700181C40020336693000124473580E2262551413183E14002400007F045E772F0001020D0100010E26000040010000010070005D01590000021337020004310700182D400203365D0000124473580E22625514131811140C2400007A045E5E610001020D010001054E000040010000010070005D01590000061337020004310700183440020334520000124473580E22625514131811140C2400007A045E5E540001020D010001054E000040010000010070005D015900003C0E0004000400010018336F597D1311471D0E43311F45007159241C7F7F1C00000C7800037F7F7F0001020033007F0B2403014003200001000000000C586600330E0004000400010018346F59740341301D0E43311F45007159241C7F7F1C00000C7800037F7F7F0001000031007F0B4103014001200001000000000C5866000118000000012709002424400D73424F00000828002C701D3B6F13122F43176B7F301229047B4900000102000100010B09000040004000010070005D015900000B1F0000000305090018183800785B796100002443593037017F161135620A001600004906000000000002000101010623010041000000010070005D01590000221F0000000305090018003800785B796100002443593042017F161135620A001C00003806000000000002000101010623010041000000010070005D015900006D150200000527090318084700785379610000244359304C017F6928356278001C00004F06000000000002000101010108010041000000010070005D01590000411400010004000939241D4074004832160012702F7F00000C59201C7F7F1E00000C7861077F7F7F00000C5833007F0B0500004000200001000000000C586600371400010004000939243B7F7420082B160012704A7F00000C59201C7F7F1E00000C7861077F7F7F00000C5833007F0B0500004001200001000000000C58660000230000001300090518183F0D1B64302C00080D4349702348161700356223000500006F064D4B6D000002000000010B1101007F000000010070005D0159000000230000000D03040030003F0D345C321C0008194D4970237F161700356223000500006F065D4B34000002000000010B0E01007F000000010070005D015900002A14000100040009390C007E6D0003453C00002F507E0E000C30241C7F7F1E00000C78610700007F0000020033007F0B0400004003200001000000000C5866002A140001000400093918007E5400036400000028137E58110C302E002D474000000C78190700007F0000020033007F0B0400004003200001000000000C58660049140001000400093924243E620042320500122F477E54000C30161D7F7F1500000C78610700007F0000020031007F0B0400004000200001010000000C5866000B1400010004000939242D3E620002211F23122F000C30477E111A1D7F7F1A00000C7861070000000000020031007F0B0400004001400001000000000C586600031F000000190009001800400B335200270000675D211625297F0A111B0000001F23005D034C697F01000200010101051E010040000000010070005D01590000031F000000190009001800401C3352003B00006173211625297F0A111B0000001F23007F034C697F01000200010101086E010040000000010070005D01590000011F00000019000900180040573352003100003F6F211625297F0A111B0000001F23007F037F737F010002000101010E4B010053000000010170005D01590000022B000000020F0900180040576F5200310000406B211625297F0A111B2900001F23007F037F737F000002000101010E4B0B0B3A000000010070005D0159000001060508000B290A00180C40571A52162D00273E6F211625297F0A181B0009001F23007F037F7378010002000101010E4B010053000000010070005D01590000011F00000019000900180040383352003400135C6F211625297F0A121B4400001F23007F037F737F000002080101010B0C010053000000010070005D0159000023160000000B000800242A60663213391A000054264A00286A55111D6A6217002E00000101000000000102000100020B08010040000000010070005D015900005B160000000B000800242A606632134D1800004A345700286A5500296A6222002E00000101000000000102000100020B08010040000000010170005D01590000081402010015080A000009343433567E540005222B0423257700001A236711001B0000220738730001000200010102054D0A004C000000010001005D01590000020E04010019430A000018403423167B3B000538481825004A7F16371A6425001B0000220700730001000200010001000E012F4C000000010070005D01590000011206010017150A000000343423567B3B000559591825004A7F322A1A6468001B000022070073000100020001000206660A004C000000010070005D01590000021207010017160A000000304C23524F3B0005766B1825004A7F162A1A6468001B000022070073000100020001000206660A004C000000010070005D015900000419000000094A0B0018273B35632E640E1E0645344E350059000E00007F00004E00002402000000010002000101010B0E010040034000010001005D0159000003190000001B450B0018273B35632E64001E0045344E35005B000E00007F00004E00003502000000010002000101010B0E010040034000010070005D015900000019000000134A0B00182E3B35632E64001E0045344E350059000E00007F00004E00002402007200010002000101010B0E010040034000010070005D015900000019000000001F0B00182E3B43632A34001E0040344E350059000E00007F00004E00002402007300010002000101010B0E010040034000010070005D01590000001C0000000212090018184043632A32001E0045344E350059040E00007F00004E00002402000000010002000101010B0E010040014000010070005D01590000410C0F07001300090524243A4B315643270000154A5B3F006722650A63357400470033120A3A0A00000102000100010B0E0D0040004000010070005D0159000013080F07001300090524243A4B5D763D4000001A305B3F006722650A633574004700330D0A000000000102000100010B0E0D0040004000010070005D01590000160B02070003250A0024243C4B5656491B000033645B66006722650A6335740066085C110A000A00000102000100010B0E0D0040004000010070005D01590000620902070003250A0024243D4B566D381B0000526439660067221C1563351C0066085C110A000A00000102000100010B0E0D0040004000010070005D01590000620902070003250A002418414B566D2328000032642F66006722191563351B0066085C110A000000000102000100010B0E0D0040004000010070005D01590000090902070003250A0024184065566B0024000053642F1400672219157B351B0066085C110A000000000102000100010B0E0D0040004000010070005D01590000001F000000030D0002000724347F767F73000E614919251E007F00181E0000002E0000000100000000010200010001000E012F40000000010070000001590000001F000000030D000200012F342376665C000E564919251E007F003A000000002E0000000100000000010200010001000E012F40000000010070005D01590000590F020000040009000038447F601849003A001F6D581A517F0014013A0013560B0000360100000000000200000001000A010039004000010070005D01590000570302000004000900003D447F611841003A00186D581A517F0014133A00135D0B0000280100000000000200000201001D010039004000010070005D01590000530501000003000A0000392A7F611830003A00216D581A527F001317470013302E00000A01000000000002000000011026010039004000010070005D015900000B040C0000040009001C30447F61190E673A002162581A517F00140B3B0013212D00001D0100000000000200010001016E01003E004000010070005D0159000022050A05000A1A0A050033011E611A55002E0B0A70581A0B6E5D5D011C426F052600000508000000000002000100010E0F0B1942004000010001005D01590000221B7F0B010D130A010039461E611834002E061275581A0B6A3E5D011C427605260000050800000000000200010001000D011948004000010001005D01590000052A7F0B010D130A01003E461E611A55002E3A1775581A0B6A555D011C4273052600000508000000000002000100010D19011948004000010001005D01590116040D0507000D130A01003B462B611A55002F060775581A0B6E5D5D2A1C426F0526000005080000000000020001000100000B1948004000010101215D01590000000D030100032709001836202F730000080F001C4B7F1100006F00203700001A2600007F037000000100000C2303010E600C007F014000010000006F00197F00007F000300051209001836152F6F00000800001F607F1100006F00201300001A2600007F037000000000000C2303010E3B0C007F016000010100006F00190000007F000600051209001839155B6F000D12000075737F1100006F001E0000001A2600007F037000000100007F23030105440C007F036000010000006F00190000007F0001000400000000297F4658007E200E4E366E1D7F01006F00091C00001900000018067F004201000007230301057A0C007F036000010000006F00190000007F0001000400000000147F46580046460E28326E5C7F00006F00001C00001900000018067F004201000007230301057A0C007F036000010000006F00190000007F0002000400000000147F3858003C560E28436E7F6400006F001C1C00000020000009097F00680100020723030101180C007F036000010000006F0019000002110A02000511090000147F0B37137F0000280C6D7F4603006F0A012E000A24007F0079047F00000000000721030104190C004D014000010000006F00000000007F00030033490B00000D76555B0500060E191E725464007F0300123F0700256500007F037F00000100000723030106320C007F036000010000006F00190000000D000B020A160A00000D76384401003B0E133B723264157F0300181B00001D007F007B0313007B01000007230301057F0C007F036000010000006F0019007FF7";

	public static String[] createProgrammNumbers() {
		String[] retarr = new String[PROGRAM_COUNT_IN_BANK];
		String[] names = DriverUtil.generateNumbers(1, PROGRAM_COUNT_IN_BANK, "Patch #000");
		System.arraycopy(names, 0, retarr, 0, PROGRAM_COUNT_IN_BANK);

		return retarr;
	}

	public static byte[] getDefaultSinglePatch() {
		byte sysex[] = new byte[PROGRAM_SIZE_SYSEX];

		System.arraycopy(Atcx.DEFAULT_PATCH_HEADER, 0, sysex, 0, Atcx.HEADER_SIZE);

		System.arraycopy(HexaUtil.convertStringToSyex(DEFAULT_PROGRAM_STRING), 0, sysex, Atcx.HEADER_SIZE, Atcx.PROGRAM_SIZE);
		sysex[PROGRAM_SIZE_SYSEX - 1] = (byte) 0xF7;
		return sysex;
	}

	public static byte[] getDefautltBankPatch() {
		return HexaUtil.convertStringToSyex(DEFAULT_BANK_STRING);
	}

}
