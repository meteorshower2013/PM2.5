package cn.edu.sendimage.PM25.conmonio.Utils;


import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;



import java.io.File;
import java.util.concurrent.TimeUnit;
public class Example3 {
    public static void main(String[] args) throws Exception{
    	String filepath1 = "D:\\Litongwen\\PM25\\demoData\\satelliteAOD\\original";;
		String filepath2= "D:\\Litongwen\\PM25\\demoData\\retrieved_PM25_auxiliary_data\\originalWeather";
		String filepath3="D:\\Litongwen\\PM25\\demoData\\station_PM25\\observed\\txt";
		File file1 = new File(filepath1);
		File file2 = new File(filepath2);
		File file3 = new File(filepath3);
        // 轮询间隔 60 秒
        long interval = TimeUnit.SECONDS.toMillis(10);
        // 创建一个文件观察器用于处理文件的格式
        FileAlterationObserver observer1 = new FileAlterationObserver(file1);
        //设置文件变化监听器
        observer1.addListener(new conmonioFileListener());
        
        // 创建一个文件观察器用于处理文件的格式
        FileAlterationObserver observer2 = new FileAlterationObserver(file2);
        //设置文件变化监听器
        observer2.addListener(new conmonioFileListener());
        
     // 创建一个文件观察器用于处理文件的格式
        FileAlterationObserver observer3 = new FileAlterationObserver(file3);
        //设置文件变化监听器
        observer3.addListener(new conmonioFileListener());
        FileAlterationMonitor monitor = new FileAlterationMonitor(interval);
        monitor.addObserver(observer1);
        monitor.addObserver(observer2);
        monitor.addObserver(observer3);
        monitor.start();
        //Thread.sleep(30000);
        //monitor.stop();
    }
}

