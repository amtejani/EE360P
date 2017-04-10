import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.StringTokenizer;

// Do not change the signature of this class
public class TextAnalyzer extends Configured implements Tool {

    // Replace "?" with your own output key / value types
    // The four template data types are:
    //     <Input Key Type, Input Value Type, Output Key Type, Output Value Type>
    public static class TextMapper extends Mapper<LongWritable, Text, Text, Tuple> {
        public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException
        {
            ArrayList<String> tokens = new ArrayList<>();
            String val = value.toString();
            val = val.toLowerCase();
            val = val.replaceAll("[^A-Za-z0-9]", " ");
            StringTokenizer itr = new StringTokenizer(val);
            while(itr.hasMoreTokens()) {
		String s = itr.nextToken();
		tokens.add(s);
            }
	    Set<String> keys = new HashSet<>();
            for(int i = 0; i < tokens.size(); i++) {
		if(!keys.contains(tokens.get(i))) {
		    keys.add(tokens.get(i));
	            for(int j = 0; j < tokens.size(); j++) {
                    	if(i != j) {
                            Tuple t = new Tuple(tokens.get(j), 1);
                            context.write(new Text(tokens.get(i)), t);
                	}
                    }
		}
            }
        }
    }

    // Replace "?" with your own key / value types
    // NOTE: combiner's output key / value types have to be the same as those of mapper
    public static class TextCombiner extends Reducer<Text, Tuple, Text, Tuple> {
        public void reduce(Text key, Iterable<Tuple> tuples, Context context)
            throws IOException, InterruptedException
        {
            Map<String,Integer> newTuples = new TreeMap<>();
            for(Tuple t: tuples) {
                String s = t.getQueryword().toString();
                if(newTuples.containsKey(s)) {
                    newTuples.put(s, t.getOccurences().get() + newTuples.get(s));
                } else {
                    newTuples.put(s, t.getOccurences().get());
                }
            }
            for(Map.Entry<String, Integer> entry: newTuples.entrySet()) {
                Tuple t = new Tuple(entry.getKey(),entry.getValue());
                context.write(key,t);
            }
        }
    }

    // Replace "?" with your own input key / value types, i.e., the output
    // key / value types of your mapper function
    public static class TextReducer extends Reducer<Text, Tuple, Text, Text> {
        private final static Text emptyText = new Text("");
        private Text queryWordText = new Text();

        public void reduce(Text key, Iterable<Tuple> queryTuples, Context context)
            throws IOException, InterruptedException
        {
            // Implementation of you reducer function
            Map<String, Integer> map = new TreeMap<>();
            for(Tuple t: queryTuples) {
                String s = t.getQueryword().toString();
                if(map.containsKey(s)) {
                    map.put(s, t.getOccurences().get() + map.get(s));
                } else {
                    map.put(s, t.getOccurences().get());
                }
            }

            // Write out the results; you may change the following example
            // code to fit with your reducer function.
            //   Write out the current context key
            context.write(key, emptyText);
            //   Write out query words and their count
            for(String queryWord: map.keySet()){
                String count = map.get(queryWord).toString() + ">";
                queryWordText.set("<" + queryWord + ",");
                context.write(queryWordText, new Text(count));
            }
            //   Empty line for ending the current context key
            context.write(emptyText, emptyText);
        }
    }

    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();

        // Create job
        Job job = new Job(conf, "EID1_EID2"); // Replace with your EIDs
        job.setJarByClass(TextAnalyzer.class);

        // Setup MapReduce job
        job.setMapperClass(TextMapper.class);
        //   Uncomment the following line if you want to use Combiner class
        job.setCombinerClass(TextCombiner.class);
        job.setReducerClass(TextReducer.class);

        // Specify key / value types (Don't change them for the purpose of this assignment)
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        //   If your mapper and combiner's  output types are different from Text.class,
        //   then uncomment the following lines to specify the data types.
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Tuple.class);

        // Input
        FileInputFormat.addInputPath(job, new Path(args[0]));
        job.setInputFormatClass(TextInputFormat.class);

        // Output
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.setOutputFormatClass(TextOutputFormat.class);

        // Execute job and return status
        return job.waitForCompletion(true) ? 0 : 1;
    }

    // Do not modify the main method
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new TextAnalyzer(), args);
        System.exit(res);
    }

    // You may define sub-classes here. Example:

}
class Tuple implements WritableComparable<Tuple> {
    private Text queryword;
    private IntWritable occurences;

    public Tuple(Text queryword, IntWritable occurences) {
        set(queryword,occurences);
    }

    public Tuple() {
        set(new Text(), new IntWritable());
    }

    public Tuple(String queryword, int occurences) {
        set(new Text(queryword), new IntWritable(occurences));
    }

    public Text getQueryword() {
        return queryword;
    }

    public IntWritable getOccurences() {
        return occurences;
    }

    public void set(Text queryword, IntWritable occurences) {
        this.queryword = queryword;
        this.occurences = occurences;
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        queryword.readFields(in);
        occurences.readFields(in);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        queryword.write(out);
        occurences.write(out);
    }

    @Override
    public String toString() {
        return queryword + " " + occurences;
    }

    @Override
    public int compareTo(Tuple tp) {
        int cmp = queryword.compareTo(tp.queryword);

        if (cmp != 0) {
            return cmp;
        }

        return occurences.compareTo(tp.occurences);
    }


    @Override
    public int hashCode(){
        return queryword.hashCode()*163 + occurences.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof Tuple)
        {
            Tuple tp = (Tuple) o;
            return queryword.equals(tp.queryword) && occurences.equals(tp.occurences);
        }
        return false;
    }
}



