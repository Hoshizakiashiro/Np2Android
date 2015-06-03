package jp.sawada.np2android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import android.util.Log;

class ScanCode {

	private int[] np2Code = new int[600];
	private int[] KeyMap = {
	
			1	,	117		,//ESC
			2	,	1		,//1
			3	,	2		,//2
			4	,	3		,//3
			5	,	4		,//4
			6	,	5		,//5
			7	,	6		,//6
			8	,	7		,//7
			9	,	8		,//8
			10	,	9		,//9
			11	,	10		,//0
			12	,	11		,//-
			13	,	12		,//^
			14	,	14		,//BS
			15	,	15		,//TAB
			16	,	16		,//Q
			17	,	17		,//W
			18	,	18		,//E
			19	,	19		,//R
			20	,	20		,//T
			21	,	21		,//Y
			22	,	22		,//U
			23	,	23		,//I
			24	,	24		,//O
			25	,	25		,//P
			26	,	26		,//@
			27	,	27		,//[
			28	,	28		,//RETURN
			29	,	116		,//CTRL
			30	,	29		,//A
			31	,	30		,//S
			32	,	31		,//D
			33	,	32		,//F
			34	,	33		,//G
			35	,	34		,//H
			36	,	35		,//J
			37	,	36		,//K
			38	,	37		,//L
			39	,	38		,//;
			40	,	39		,//:
				
			42	,	112		,//SHIFT
			43	,	40		,//]
			44	,	41		,//Z
			45	,	42		,//X
			46	,	43		,//C
			47	,	44		,//V
			48	,	45		,//B
			49	,	46		,//N
			50	,	47		,//M
			51	,	48		,//,
			52	,	49		,//.
			53	,	50		,//SLASH
			54	,	112		,//SHIFT
			55	,	69		,//[*]
			56	,	115		,//Alt L → GRPH
			57	,	52		,//SPACE
			58	,	113		,//CAPS
			59	,	98		,//f1
			60	,	99		,//f2
			61	,	100		,//f3
			62	,	101		,//f4
			63	,	102		,//f5
			64	,	103		,//f6
			65	,	104		,//f7
			66	,	105		,//f8
			67	,	106		,//f9
			68	,	107		,//f10
			69	,	62		,//Num Lock → HOME
			70	,	63		,//Scroll Lock → HELP
			71	,	66		,//[7]
			72	,	67		,//[8]
			73	,	68		,//[9]
			74	,	64		,//[-]
			75	,	70		,//[4]
			76	,	71		,//[5]
			77	,	72		,//[6]
			78	,	73		,//[+]
			79	,	74		,//[1]
			80	,	75		,//[2]
			81	,	76		,//[3]
			82	,	78		,//[0]
			83	,	79		,//[,]
			87	,	77		,//F11 → [=]
			88	,	80		,//F12 → [.]
			89	,	51		,//BACKSLASH
			92	,	53		,//Henkan → XFER
			93	,	114		,//Katakana → KANA
			94	,	81		,//Muhenkan → NFER
			96	,	28		,//[RETURN]
				
			98	,	65		,//[/]
			99	,	97		,//Print → COPY
				
				
			102	,	62		,//HOME
			103	,	58		,//UP
			104	,	54		,//ROLL UP
			105	,	59		,//LEFT
			106	,	60		,//RIGHT
			107	,	63		,//End → HELP
			108	,	61		,//DOWN
			109	,	55		,//ROLL DOWN
			110	,	56		,//INS
			111	,	57		,//DEL
			114	,	123		,//Vol Down
			115	,	124		,//Vol Up
			119	,	96		,//STOP
			124	,	13		,//\
				
				
			127	,	120		,//MENU
			139	,	120		,//MENU
			158	,	119		,//back → RIGHT CLICK
			217	,	15		,//Search → TAB
			229	,	120		,//MENU
			232	,	118		,//DPAD Center → LEFT CLICK
			248	,	116		,//e_kao_ki → CTRL
			249	,	114		,//moji → KANA

		};
	
	public ScanCode(String keymapfile) {
        
		for (int j = 0 ; j < KeyMap.length ;j+=2) {
			
			int ScanCode = KeyMap[j];
			int Np2Code = KeyMap[j+1];
			
			np2Code[ScanCode] = Np2Code;
		}

		ReadKeyMapFile(keymapfile);
	}
	
	
	void ReadKeyMapFile(String KeyMapFile) {
		
		if ( KeyMapFile != null ) {
			try {
				File _keymap = new File(KeyMapFile); // keymapファイル
				Log.d("SDL","File " + KeyMapFile + " Found");

				BufferedReader br = new BufferedReader(new FileReader(_keymap));

				// 最終行まで読み込む
				String line = "";
				while ((line = br.readLine()) != null ) {
					Log.d("SDL","readline=(" + line +")");
					SetKeyMap(line);
				}
				br.close();

			} catch (FileNotFoundException e) {
				// Fileオブジェクト生成時の例外捕捉
				Log.d("SDL","File Not Found");
			} catch (IOException e) {
				// BufferedReaderオブジェクトのクローズ時の例外捕捉
				Log.e("SDL","IO Error!");
			}
		}
	}

	void SetKeyMap(String line) {

		// 1行をデータの要素に分割
		String[] st = line.split(",");

		try{
			int ReadScanCode = Integer.parseInt(st[0]);
			int ReadNp2Code = Integer.parseInt(st[1]);

			np2Code[ReadScanCode] = ReadNp2Code;

		} catch(NumberFormatException e) {
			// 非数時の例外捕捉
			Log.e("SDL","Not a Number!");
		}

	}

	public int toNp2Code(int Code){
		
		return np2Code[Code];	
	}
	
	
}