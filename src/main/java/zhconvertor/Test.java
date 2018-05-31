package zhconvertor;


import zhconvertor.util.ZHConverter;

public class Test{
	public static void main(String[] args){
		ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);
		String simplifiedStr = converter.convert("草泥馬個比比了個比比喝水和氣身材興奮生意");
		System.out.println(simplifiedStr);
	}
}
