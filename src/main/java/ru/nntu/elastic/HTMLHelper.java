package ru.nntu.elastic;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.collect.HppcMaps;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import ru.nntu.elastic.Utils.Consts;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

/**
 * Created by RTCCD on 03.03.2016.
 */
public class HTMLHelper {
    private static TransportClient client;
    private static void init(){
        Settings settings = Settings.settingsBuilder().put("cluster.name", Consts.CLUSTER)
                .build();
        client = new TransportClient.Builder().settings(settings).build();
        try {
            client.addTransportAddress(new InetSocketTransportAddress(
                    InetAddress.getByName(Consts.HOST),
                    Consts.PORT
            ));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        client.admin()
                .indices()
                .prepareRefresh()
                .execute()
                .actionGet();
    }

    public static String printDocument(String value){
        init();
        StringBuilder sb = new StringBuilder();
        SearchResponse response = client.prepareSearch(Consts.INDEX)
                .setTypes(Consts.TYPE)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.matchQuery("firstname",value))
                .execute().actionGet();
        SearchHit[] results = response.getHits().getHits();
        for(SearchHit hit : results){
            Map<String, Object> result = hit.getSource();
            for(Map.Entry<String, Object> entry : result.entrySet()){
                String attr = entry.getKey();
                String val = (String) entry.getValue();
                sb.append(attr + " : " + val);
                sb.append("</br>");
            }

        }
        return sb.toString();
    }


}
