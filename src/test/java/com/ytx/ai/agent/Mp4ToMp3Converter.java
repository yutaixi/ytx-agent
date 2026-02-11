//package com.ytx.ai.agent;
//
//import cn.hutool.core.io.FileUtil;
//import net.bramp.ffmpeg.FFmpeg;
//import net.bramp.ffmpeg.FFmpegExecutor;
//import net.bramp.ffmpeg.builder.FFmpegBuilder;
//
//import java.io.File;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//
//
//public class Mp4ToMp3Converter {
//
//    public static void main(String[] args) {
//        // 输入 MP4 文件路径
//        String inputFilePath = "V:\\海底小纵队\\Octonauts S4";
//        List<File> files=getAllFiles(inputFilePath);
//        // 输出 MP3 文件路径
//        String outputFilePath = "F:\\download\\音频/";
//        files.forEach(item->{
//
//            try {
//                // 指定 FFmpeg 可执行文件路径
//                String ffmpegPath = "ffmpeg"; // 如果 FFmpeg 在 PATH 中，直接写 "ffmpeg"，否则写绝对路径
//                String mp3Name= FileUtil.getPrefix(item.getName())+".mp3";
//
//                // 初始化 FFmpeg
//                FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
//                FFmpegExecutor executor = new FFmpegExecutor(ffmpeg);
//
//                // 构建 FFmpeg 命令
//                FFmpegBuilder builder = new FFmpegBuilder()
//                        .setInput(item.getAbsolutePath()) // 输入文件
//                        .overrideOutputFiles(true) // 允许覆盖输出文件
//                        .addOutput(outputFilePath+mp3Name) // 输出文件
//                        .setFormat("mp3") // 输出格式为 MP3
//                        .addExtraArgs("-q:a", "0") // 设置高质量音频
//                        .done();
//
//                // 执行命令
//                executor.createJob(builder).run();
//
//                System.out.println("音频提取完成，保存为: " + outputFilePath);
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.err.println("处理失败: " + e.getMessage());
//            }
//        });
//
//    }
//
//
//    private static List<File> getAllFiles(String directory){
//        File directoryFile = new File(directory);
//        if (directoryFile.isDirectory()) {
//            File[] files = directoryFile.listFiles();
//            if (files != null) {
//                return Arrays.stream(files)
//                        .filter(File::isFile)
//                        .filter(item->item.getName().endsWith(".mkv"))
//                        .collect(Collectors.toList());
//            }
//        }
//        return Collections.emptyList();
//    }
//}
