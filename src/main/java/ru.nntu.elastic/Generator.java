package ru.nntu.elastic;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.util.CancellableThreads;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Created by RTCCD on 02.03.2016.
 */
public class Generator {

    private static final String host = "localhost";
    private static final String cluster = "elasticsearch";
    private static final int port = 9300;
    private static final int count = 50;

    private static ArrayList<String> skills = new ArrayList<String>(){{
        add("Java");
        add("C/C++");
        add("Scala");
        add("PHP");
        add("C#");
        add("Python");
        add("JavaScript");
        add("Haskel");
    }};

    private static ArrayList<String> countries = new ArrayList<String>(){{
        add("Russia");
        add("Sweden");
        add("France");
        add("Brazil");
        add("Finland");
        add("Poland");
        add("Ukraine");
    }};

    private static String getRandomName(int length){
        char[] characters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i <= length; i++){
            sb.append(characters[getRandomNumber(0,25)]);
        }

        return StringUtils.capitalize(sb.toString());
    }

    private static String getRandomSkills(int amount){
        StringBuilder sb = new StringBuilder();

        ArrayList<String> randSkills = new ArrayList<String>();
        for(int i = 0; i < amount; i++){
            String skill = skills.get(getRandomNumber(0, skills.size()));
            if(!randSkills.contains(skill)){
                randSkills.add(skill);
            }else {
                i--;
            }
        }

        return randSkills.stream().reduce("",(acc, item) -> acc + item + ", ");
    }

    private static String getRandomDateOfBirth(){
        GregorianCalendar gc = new GregorianCalendar();
        int year = getRandomNumber(1970, 2000);
        gc.set(Calendar.YEAR, year);
        int month = getRandomNumber(1, gc.getActualMaximum(Calendar.MONTH));
        gc.set(Calendar.MONTH, month - 1);
        int day = getRandomNumber(1, gc.getActualMaximum(Calendar.DAY_OF_MONTH));
        gc.set(Calendar.DAY_OF_MONTH, day);
        return new SimpleDateFormat("dd/MM/yyyy").format(gc.getTime());
    }

    private static int getRandomNumber(int start, int end){
        return new Random().nextInt(end - start) + start;
    }
    public static void main(String[] args) throws Exception {

        Settings settings = Settings.settingsBuilder().put("cluster.name", cluster)
                                                      .build();
        TransportClient client = new TransportClient.Builder().settings(settings).build();
        client.addTransportAddress(new InetSocketTransportAddress(
                InetAddress.getByName(host),
                port
        ));

        client.admin().indices().prepareRefresh().execute().actionGet();

        for(int i = 0; i <= count; i++){
            IndexResponse response = client.prepareIndex("habrauser","users", Integer.toString(i))
            .setSource(
                    jsonBuilder().startObject()
                                 .field("firstname", getRandomName(getRandomNumber(4,10)))
                                 .field("surname", getRandomName(getRandomNumber(4,13)))
                                 .field("birthDate", getRandomDateOfBirth())
                                 .field("location", countries.get(getRandomNumber(0,countries.size()  -1)))
                                 .field("skills", getRandomSkills(getRandomNumber(1,skills.size() - 1)))
                                 .endObject()
            ).get();
            System.out.println("Index: " + response.getIndex());
            System.out.println("Type: " + response.getType());
            System.out.println("ID: " + response.getId());
            System.out.println("Version: " + response.getVersion());
            if(response.isCreated())
                System.out.println("Document created");

        }

    }
}
