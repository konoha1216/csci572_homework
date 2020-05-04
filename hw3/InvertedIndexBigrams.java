import java.io.IOException;
import java.util.StringTokenizer;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Writable;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class InvertedIndexBigrams {

  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, Text>{

    // change this param
    private Text BIGRAM = new Text();
    private String docId = "";
    private boolean flag = true;

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      String line = value.toString().toLowerCase();
      String []parts = line.split("\\t",2);
      docId = parts[0];
      String docContent = parts[1].replaceAll("[^a-z]+", " ");
      StringTokenizer itr = new StringTokenizer(docContent);

      // new lines
      String prev = null;

      while (itr.hasMoreTokens()) {
        String cur = itr.nextToken();
        if(prev!=null){
          BIGRAM.set(prev + " " + cur);
          context.write(BIGRAM, new Text(docId));
        }
        prev = cur;
      }
    }
  }

  public static class InvertReducer
       extends Reducer<Text,Text,Text,Text> {

    public void reduce(Text key, Iterable<Text> values,
                       Context context
                       ) throws IOException, InterruptedException {
      HashMap<String,Integer> result = new HashMap<>();
      for (Text val : values) {
        String string = val.toString();
        result.put(string, result.getOrDefault(string, 0) + 1);
      }
      StringBuilder sb=new StringBuilder("");
      Iterator mapIter = result.entrySet().iterator();
      while(mapIter.hasNext()){
        sb.append(" ");
        Map.Entry mapElement = (Map.Entry)mapIter.next(); 
        sb.append(mapElement.getKey()+":"+mapElement.getValue());
      } 
      context.write(key, new Text(sb.toString()));
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "invert index");
    job.setJarByClass(InvertedIndexBigrams.class);
    job.setMapperClass(TokenizerMapper.class);
    // job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(InvertReducer.class);

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}