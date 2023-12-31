package me.LiveSongs;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


public class Utils {
    final static int VIDEO_TYPE = 11;
    public static final String M_4_A = ".m4a";

    public synchronized static void flushIdleSongs(boolean is) {
        LiveSongs.isongs = new File(LiveSongs.WORK_DIR + "\\IdleMusics");
        if (LiveSongs.isongs.listFiles() == null) {
            LiveSongs.warnly("[点歌] 没有空闲歌曲");
            return;
        }
        if(!is){
            LiveSongs.isongPath.clear();
            LiveSongs.isongPath.addAll(Arrays.asList(LiveSongs.isongs.listFiles()));
        }
        if (!LiveSongs.randomIdles) {
            return;
        }
        if (List.of(LiveSongs.isongs.listFiles()).equals(LiveSongs.nativeFiles) && is) {
            return;
        }



        List<File> list = new ArrayList<>(LiveSongs.isongPath);
        Collections.shuffle(list);
        LiveSongs.isongPath.clear();
        LiveSongs.isongPath.addAll(list);

        LiveSongs.nativeFiles = List.of(LiveSongs.isongs.listFiles());
        System.out.println(LiveSongs.isongPath);
    }

    public static String searchSongs(String message){

        String url = String.format("https://api.bilibili.com/x/web-interface/search/all/v2?page=1&keyword=%s", message);
        List<String> keywords = LiveSongs.pbList;
        String result = getWebResult(url);
        //我好像也看不懂啊

        try {
            JsonObject searchResult = new Gson().fromJson(result, JsonObject.class).getAsJsonObject();
            JsonObject pageInfo = searchResult.get("data").getAsJsonObject().get("pageinfo").getAsJsonObject();
            int numResults = pageInfo.get("video").getAsJsonObject().get("numResults").getAsInt();
            if (numResults == 0) {
                return "nosongs";
            }
            JsonArray searchTypes = searchResult.get("data").getAsJsonObject().get("result").getAsJsonArray();
            JsonObject type = searchTypes.get(VIDEO_TYPE).getAsJsonObject();
            JsonArray videos = type.get("data").getAsJsonArray();
            video_filter : for (int j = 0; j < videos.size(); j++) {

                JsonObject video = videos.get(j).getAsJsonObject();
                String tags = video.get("tag").getAsString();
                List<String> tagList = Arrays.asList(tags.split(","));//这都嘛这是草
                if (Collections.disjoint(tagList, keywords) &&
                        !keywords.contains(video.get("typename").getAsString())) {
                    for(String target : keywords){
                        if(video.get("title").getAsString().contains(target)){
                            continue video_filter;
                        }
                    }//我去看一下
                    /*
                    LiveMSC 0.5.0更新计划
[+]点歌权限（房管/观  >      我写
                    我试试
                    你先告诉我怎么获取那些玩意
                    弹幕还有发送人
                    然后
                    受到弹幕的监听器事件在LiveSongs->LiveRoom->message()
                     */
                    //那写什么
                    if(!tags.contains(LiveSongs.onlyPlay) && !"无".equals(LiveSongs.onlyPlay)){
                        continue;
                    }
                    String videoBVID = video.get("bvid").getAsString();

                    if(LiveSongs.blackMusic.contains(videoBVID)){
                        return "nosongs";
                    }
                    String title = video.get("title").getAsString()
                            .replaceAll("<em class=\"keyword\">", "")
                            .replaceAll("</em>", "");
                    return videoBVID +
                            "á" +
                            title;
                }
            }
        } catch (Exception e){
            LiveSongs.errorly("[点歌] 搜索出现问题，原因为");
            LiveSongs.errorly(result);
            if(result == null){
                LiveSongs.infoly("[点歌] 请求失败！");
                return "nosongs";
            }


            if(result.contains("请求被拦截")){
                LiveSongs.errorly("[点歌] 请求被拦截，请尝试刷新Cookie");
            }
            return "nosongs";
        }
        return "nosongs"; 
    }

    public static List<File> randomArray(List<File> fileList) {
        /*Random random = new Random();
        ArrayList<File> result = new ArrayList<>(fileList);
        for (int i = 0; i < fileList.size(); i++) {
            int index = random.nextInt(fileList.size() - i) + i;
            File temp = result.get(i);
            result.set(i, result.get(index));
            result.set(index, temp);
            System.out.println("随机循环" + i + "次");
        }*/
        Collections.shuffle(fileList);
        return new ArrayList<>(fileList);
    }

    public static boolean isEf(boolean ef, File aFile, StringBuilder ffmpegCommand) throws IOException, InterruptedException {
        LiveSongs.infoly("[点歌] FFmpeg指令：" + ffmpegCommand);

        ProcessBuilder pb = new ProcessBuilder(splitStringWithoutCharsInQuotes(String.valueOf(ffmpegCommand), ' '));
        pb.redirectErrorStream(true);
        Process process = pb.start();

        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;

        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (!line.contains("No such file or directory")) {
                sb.append("[处理] ").append(line).append('\n');
            } else {
                sb.append("[处理] ").append(line).append('\n');
                LiveSongs.errorly("[处理] 转换失败");
                ef = false;
                break;
            }
        }
        LiveSongs.infoly(sb.toString());
        if (process.isAlive()) {
            process.waitFor();
        }
        if(aFile.delete()){
            LiveSongs.infoly("[处理] 成功删除残余文件");
        }
        return ef;
    }
    public static String getWebResult(String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:102.0) Gecko/20100101 Firefox/102.0")
                .addHeader("Cookie", LiveSongs.g(true, true) + "_FNVAL=4048; CURRENT_PID=1483e220-d618-11ed-aac1-253a3b835710; rpdid=|(u))YR|mu0J'uY)|mYJRRJ; hit-new-style-dyn=1; hit-dyn-v2=1; buvid4=934B8E0E-E1A4-CC20-24EB-6D2FB8C1508270064-023040819-7IPkCYvUp8X%2FUJVJMHaawA%3D%3D; buvid_fp_plain=undefined; FEED_LIVE_VERSION=V8; CURRENT_QUALITY=64; is-2022-channel=1; SESSDATA=316606a9%2C1705328365%2C76433%2A72Kq7I1z8wLnNNH2XKjwIhGZQ-soJNqUE77Iwq4msSbMnAqH1r7jEAe0gmHIrnZat593HKywAANAA; bili_jct=54f2880f60e113f2a621ac979a57c28b; fingerprint=aee1e223f77fe66609f2929e66412549; buvid_fp=aee1e223f77fe66609f2929e66412549; bp_video_offset_484097652=821172011555356700; innersign=0; PVID=6; b_lsid=7A6AD9C5_189815FB377; sid=n7fszfbz; browser_resolution=1513-1368; Hm_lvt_8a6e55dbd2870f0f5bc9194cddf32a02=1689863081,1690092718; Hm_lpvt_8a6e55dbd2870f0f5bc9194cddf32a02=1690092718")
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        try (Response response = call.execute()) {
            if (response.body() != null) {
                return response.body().string();
            } else {
                LiveSongs.errorly("[点歌] API没有返回任何内容");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
    static String a = new String("ut=1680933857; i-wanna-go-back=");
    public static boolean isEnded(AudioPlayers audioPlayer) {
        if (!audioPlayer.isAlive()) {
            return true;
        }
        if(LiveSongs.stop) {
            audioPlayer.a.s();
            LiveSongs.stop = false;
            return true;
        }
        return false;
    }
    static String c = "09544592942; _uuid=294F5F91-6";
    public static void downloadSongs(String video, String pName, boolean notWaitList){
        if(!notWaitList){

            if (video == null || "nosongs".equals(video)) {
                LiveSongs.errorly("[处理] 未找到歌曲");
                return;
            }
            String videoBvid = video.split("[á]")[0];
            String videoTitle = video.split("[á]")[1];
            try {
                String bbdownPath = LiveSongs.WORK_DIR + "\\lib\\API\\BBDown.exe";
                String ffmpegPath = LiveSongs.WORK_DIR + "\\lib\\FFmpeg\\ffmpeg.exe";
                String idleMusicsDir = LiveSongs.WORK_DIR + "\\IdleMusics\\";
                String wavFile = idleMusicsDir + videoTitle + ".wav";
                String m4aFile = idleMusicsDir + videoTitle + ".m4a";

                if(!new File(wavFile).exists()) {
                    // Download the audio file using BBDown
                    String bbdownCommand = String.format("\"%s\" %s --audio-only -p 1 --work-dir %s --ffmpeg-path %s", bbdownPath, videoBvid, idleMusicsDir, ffmpegPath);
                    File aFile = getFile(videoTitle, idleMusicsDir, m4aFile, bbdownCommand);
                    wavFile = wavFile.replace('/', '.');
                    String ffmpegCommand = String.format("\"%s\" -y -i \"%s\" \"%s\"", ffmpegPath, LiveSongs.getFindFileI(aFile), wavFile);
                    boolean ef = isEf(true, aFile, new StringBuilder(ffmpegCommand));

                    if(!ef){
                        LiveSongs.errorly("[处理] 处理失败！");
                    }
                }else {
                    LiveSongs.infoly("[处理] 音频文件已存在");
                }
            } catch (IOException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }

        }else {
            if (video != null && !"nosongs".equals(video)) {
                String videoBvid = video.split("[á]")[0];
                String videoTitle = video.split("[á]")[1];
                if(LiveSongs.downloadInIdle){
                    new Thread(()-> {
                        String videos = Utils.searchSongs(videoBvid);
                        Utils.downloadSongs(videos, "闲置", false);
                    }).start();
                }
                try {
                    String bbdownPath = LiveSongs.WORK_DIR + "\\lib\\API\\BBDown.exe";
                    String ffmpegPath = LiveSongs.WORK_DIR + "\\lib\\FFmpeg\\ffmpeg.exe";
                    String musicsDir = LiveSongs.WORK_DIR + "\\musics\\";
                    String wavFile;
                    if(!LiveSongs.saveMusics) {
                        wavFile = musicsDir + videoTitle + new Random().nextInt(90) + ".wav";
                    }else{
                        wavFile = musicsDir + videoTitle +".wav";
                    }
                    String m4aFile = musicsDir + videoTitle + ".m4a";

                    boolean ef = true;
                    if (!new File(wavFile).exists() || !LiveSongs.saveMusics) {
                        // Download the audio file using BBDown
                        String bbdownCommand = String.format("\"%s\" \"%s\" --audio-only -p 1 --work-dir \"%s\" --ffmpeg-path \"%s\"", bbdownPath, videoBvid, musicsDir, ffmpegPath);
                        File aFile = getFile(videoTitle, musicsDir, m4aFile, bbdownCommand);
                        try {
                            videoTitle = getFindFile(aFile).getName();
                        } catch (NullPointerException e) {
                            LiveSongs.errorly("[处理] 点歌失败");
                            LiveSongs.errorly("[处理] 视频可能为多p");
                            return;
                        }
                        videoTitle = videoTitle.replaceAll(".m4a", "")
                                .replaceAll(".wav", "");
                        wavFile = wavFile.replace('/', '.');
                        String ffmpegCommand = String.format("\"%s\" -y -i \"%s\" \"%s\"", ffmpegPath, getFindFile(aFile), wavFile);
                        ef = isEf(ef, aFile, new StringBuilder(ffmpegCommand));
                    } else {
                        LiveSongs.warnly("[处理] 音频文件已存在");
                    }
                    if (ef) {
                        LiveSongs.infoly("[处理] 转换完成：" + videoTitle);
                        File songFile = new File(wavFile);
                        if (!LiveSongs.allowReplay) {
                            if (!LiveSongs.waitToPlay.contains(songFile)) {
                                LiveSongs.addMusic(pName, songFile);
                                if (LiveSongs.isIdle && LiveSongs.breakIdles) {
                                    LiveSongs.stop = true;
                                }
                            } else {
                                LiveSongs. warnly("[处理] 不允许重复点歌");
                            }
                        } else {
                            LiveSongs. addMusic(pName, songFile);

                            if (LiveSongs.isIdle && LiveSongs.breakIdles) {
                                LiveSongs.stop = true;
                            }
                        }
                    }
                } catch (IOException | InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                LiveSongs.errorly("[点歌] 未找到歌曲");
            }

        }
    }


    static
    String b = eD("VE_BUVID=AUTO72168" + c + "10CC-");


    public static boolean isCharInQuotes(String str, char c, int index) {
        char[] target = str.toCharArray();

        boolean pointInQuotes = false;
        int pointIndex = 0;
        for(char i : target){
            if('\"' == i){
                pointInQuotes = !pointInQuotes;
            }else if(c == i && pointIndex == index && pointInQuotes){
                return true;
            }
            pointIndex++;
        }
        return false;
    }
    public static String eD(String y2){
        return y2;
    }

    public static String[] splitStringWithoutCharsInQuotes(String str, char delimiter) {
        //定义一个列表，用来存储分割后的子字符串
        List<String> parts = new ArrayList<String>();
        //定义一个变量start，表示当前子字符串的起始位置，初始值为0
        int start = 0;
        //用一个for循环遍历字符串中的每个字符
        for (int i = 0; i < str.length(); i++) {
            //如果当前字符等于分隔符，并且不在双引号内，就说明找到了一个子字符串的结束位置
            if (str.charAt(i) == delimiter && !isCharInQuotes(str, delimiter, i)) {
                //用substring()方法从start开始到i结束截取子字符串，并添加到列表中
                parts.add(str.substring(start, i));
                //将start更新为i加一，表示下一个子字符串的起始位置
                start = i + 1;
            }
        }
        //如果start小于字符串长度，说明最后还有一个子字符串没有添加到列表中，就再添加一次
        if (start < str.length()) {
            parts.add(str.substring(start));
        }
        //将列表转换成字符串数组，并返回结果
        return parts.toArray(new String[0]);
    }

    public static Object getKeyFromValue(Map<Character, String> map, Object value){
        for(Object o : map.keySet()){
            if(map.get(o).equals(value)){
                return o;
            }
        }
        return null;
    }

    @NotNull
    private static File getFile(String videoTitle, String idleMusicsDir, String m4aFile, String bbdownCommand) throws InterruptedException, IOException {
        LiveSongs.infoly("[处理] BBDown指令" + bbdownCommand);
        String[] commands = splitStringWithoutCharsInQuotes(bbdownCommand, ' ');
        for(int i = 0; i < commands.length; i++){
            if(i > 0){
                commands[i] = commands[i].replace("\"", "");
            }
        }

        ProcessBuilder pb2 = new ProcessBuilder(  commands                     );

        pb2.redirectErrorStream(true);
        pb2.start().waitFor();
        videoTitle = videoTitle.replace("&#39;", "'");
        LiveSongs.infoly("[处理] 下载完成：" + videoTitle);

        // Check if the file is a directory
        File videoDir = new File(idleMusicsDir + videoTitle);
        if(videoDir.isDirectory()){
            LiveSongs.warnly("[处理] 未能转换歌曲，可能是以文件夹形式存在");
            LiveSongs.warnly("[处理] 正在尝试移动文件");
            try {
                for (File tFile : videoDir.listFiles()) {
                    Files.move(Paths.get(tFile.getAbsolutePath()), Paths.get(m4aFile));
                }
            }catch (NullPointerException e){
                LiveSongs.errorly("[处理] 未能成功移动文件！");
            }
        }
        videoDir.delete();

        // Convert the m4a file to wav using FFmpeg
        File aFile = new File(m4aFile);
        return aFile;
    }

    public static Map<File, Double> sortByValue(Map<File, Double> unsortedMap) {
        return unsortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new
                ));
    }public static File getMayFile(File targetFile) {
        final String MUSIC_DIRECTORY = LiveSongs.WORK_DIR + "\\musics";

        File musicDir = new File(MUSIC_DIRECTORY);

        if (musicDir.listFiles() == null) {
            return null;
        }

        return Arrays.stream(musicDir.listFiles(file -> file.getName().endsWith(".wav")))
                .map(musicFile -> new AbstractMap.SimpleEntry<>(musicFile,
                        StrUtil.SimilarDegree(targetFile.getName(), musicFile.getName())))
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
    public static File getFindFile(File targetFile) {
        File targetFiles = new File(LiveSongs.WORK_DIR + "\\musics");

        if (targetFiles.listFiles() == null) {
            return null;
        }

        return Arrays.stream(targetFiles.listFiles())
                .map(s -> new AbstractMap.SimpleEntry<>(s,
                        StrUtil.SimilarDegree(targetFile.getName(), s.getName())))
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .filter(file -> file.getName().contains(M_4_A))
                .findFirst()
                .orElseGet(() -> new File(""));
    }


    public static File getMayFileI(File targetFile) {
        File targetFiles = new File(LiveSongs.WORK_DIR + "\\IdleMusics");

        if (targetFiles.listFiles() == null) {
            return null;
        }

        return Arrays.stream(Objects.requireNonNull(targetFiles.listFiles()))
                .map(s -> new AbstractMap.SimpleEntry<>(s,
                        StrUtil.SimilarDegree(targetFile.getName(), s.getName())))
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .filter(file -> file.getName().contains(".wav"))
                .findFirst()
                .orElse(null);
    }

    public static byte[] byteMerger(byte[] byteL, byte[] byteR){
        byte[] byteArr = new byte[byteL.length + byteR.length];
        System.arraycopy(byteL, 0, byteArr, 0, byteL.length);
        System.arraycopy(byteR, 0, byteArr, byteL.length, byteR.length);
        return byteArr;
    }
    public static byte[] hexToByteArray(String hexStr) {
        if (hexStr.length() % 2 == 1) {
            hexStr = "0" + hexStr;
        }

        int hexlen = hexStr.length();
        byte[] result = new byte[(hexlen / 2)];

        for (int i = 0, j = 0; i < hexlen; i += 2, j++) {
            result[j] = (byte)Integer.parseInt(hexStr.substring(i, i + 2),16);
        }
        return result;
    }
}
