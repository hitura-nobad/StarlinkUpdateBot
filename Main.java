import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

public class Main {
    final static JFileChooser fc = new JFileChooser();
    static ArrayList<File> outputs = new ArrayList<>();
    static HashMap<String,Date> LaunchData = new HashMap<>();
    static HashMap<String,String> LaunchName = new HashMap<>();
    public static void main(String... args) throws Exception {
        System.out.println(Arrays.toString(args));
        initMap();
        File file = new File(args[0]);
        new Main().processSingleFile(file,args[1]);
        //combine();
    }

    private void processSingleFile(File f,String out) throws Exception {
        String[] array=new FileArrayProvider().readLines(f.getAbsolutePath());
        File fout = new File(out);
        FileOutputStream fos = new FileOutputStream(fout);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write("SATNAME;LAUNCH;APOGEE;PERIGEE;MEDIAN;INCLINATION;TIMESINCELAUNCH\n");
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        for (int i = 0; i < array.length; i+=3) {
            TLE orbit = new TLE(array[i].replaceAll("\\s+", ""),array[i+1],array[i+2]);
            //Calculating Day Difference
            double dif;
            double back = LaunchData.get(orbit.getLaunchID().replaceAll("[^\\d.]", "")).getTime();
            double now =orbit.getDate().getTime();
            dif = now  - back ;
            double days= dif/(1000*60*60*24);
            //System.out.println(days);
            sb.append(orbit.getName()).append(";");
            sb.append(LaunchName.get(orbit.getLaunchID().replaceAll("[^\\d.]", ""))).append(";");
            sb.append(orbit.getApogee()+";");
            sb.append(orbit.getPerigee()+";");
            sb.append(orbit.getMeanAltitude()).append(";");
            sb.append(orbit.getInclination()+";");
            sb.append(days+"\n");
            bw.write(sb.toString());
            sb.delete(0,sb.length());
        }
        bw.close();
        outputs.add(fout);
    }
    public static<T> T[] subArray(T[] array, int beg) {
        return Arrays.copyOfRange(array, beg, array.length+1);
    }
    public static void initMap(){
        Date a = new Date();
        a.setTime(1558665000L*1000);
        Date b = new Date();
        b.setTime(1573484160L*1000);
        Date c = new Date();
        c.setTime(1578363540L*1000);
		Date d = new Date();
        d.setTime(1580306760L*1000);
        LaunchData.put("19029",a);
        LaunchName.put("19029", "Starlink V0.9");
        LaunchData.put("19074",b);
        LaunchName.put("19074","Starlink V1.0-L1");
        LaunchData.put("20001",c);
		LaunchName.put("20001","Starlink V1.0-L2");
        LaunchData.put("20006",d);
		LaunchName.put("20006","Starlink V1.0-L3");
    }



    public class TLE {
        private ArrayList<String> tleString;
        private String name;
        private Date date;
        private String LaunchID;
        private double meanMotionAtEpoch,eccentricity,inclination;
        double mu = 3.986004418 * Math.pow(10,14);
        public TLE(ArrayList<String> input) {
            tleString=input;
            try {
                parse();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public TLE(String... input) {
            tleString= new ArrayList<String>(Arrays.asList(input));
            try {
                parse();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String getLaunchID() {
            return LaunchID;
        }

        public String getName() {
            return name;
        }

        public Date getDate() {
            return date;
        }

        private void parse() throws Exception {
            name= tleString.get(0);
            String[] first = tleString.get(1).replace("  "," ").replace("  "," ").split(" ");
            String dateTemp = first[3];
            GregorianCalendar gc = new GregorianCalendar();
            int x = Integer.parseInt(dateTemp.substring(2,5));
            int y = Integer.parseInt(dateTemp.substring(0,2))+2000;
            double z = Integer.parseInt(dateTemp.substring(7,8))*24/10;
            gc.set(GregorianCalendar.YEAR,y);
            gc.set(GregorianCalendar.DAY_OF_YEAR,x);
            gc.set(GregorianCalendar.HOUR_OF_DAY,(int)(Math.round(z)));
            LaunchID = first[2];
            date = gc.getTime();
            String[] second = tleString.get(2).replace("  "," ").replace("  "," ").split(" ");
            meanMotionAtEpoch = Double.parseDouble(second[7]);
            inclination= Double.parseDouble(second[2]);
            eccentricity = Double.parseDouble("0."+second[4]);

            if (!second[0].equalsIgnoreCase("2"))throw new Exception("Invalid TLE");

        }

        public double getMeanMotionAtEpoch() {
            return meanMotionAtEpoch;
        }
        public double getSemiMajorAxis() {
            return Math.pow(mu,1.0/3.0)/Math.pow((2*Math.PI* meanMotionAtEpoch/86400),2.0/3.0);
        }
        public double getMeanAltitude(){
            return (getSemiMajorAxis() - 6378137)/1000;
        }
        public double getApogee(){
            return (getSemiMajorAxis()*(1+eccentricity) - 6378137 )/1000;
        }
        public double getPerigee(){
            return (getSemiMajorAxis()*(1-eccentricity)-6378137)/1000;
        }

        public double getInclination() {
            return inclination;
        }
    }
    public class FileArrayProvider {

        public String[] readLines(String filename) throws IOException {
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            List<String> lines = new ArrayList<String>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();
            return lines.toArray(new String[lines.size()]);
        }
    }
}
