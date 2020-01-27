package sample;

import javax.swing.text.html.HTML;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class RunLengthEncoding {
    private ArrayList<String> tags = new ArrayList<>();
    private String inputStream;
    private ArrayList<HuffmanNode> huffmanTable = new ArrayList<>();
    private HuffmanNode huffmanTree;
    private String compressedStream = "";

    public int calcCategory(int num) {
        if (num < 0) {
            num *= -1;

        }
        String bin = Integer.toBinaryString(num);
        return bin.length();
    }

    public void generateTags() {
        int zeroCounter = 0;
        String tag = "";
        String x = "";
        for (int i = 0; i < inputStream.length(); i++) {

            x += String.valueOf(inputStream.charAt(i));
            if (x.equals(String.valueOf('-'))) {
                continue;
            } else if (x.equals(String.valueOf('0'))) {
                if (zeroCounter != 0 && i == inputStream.length() - 1) {
                    tag = "EOB";
                    tags.add(tag);
                    break;
                } else {
                    zeroCounter++;
                    x = "";
                }
            } else {
                tag += zeroCounter + "/" + calcCategory(Integer.parseInt(x));
                tags.add(tag);
                tag = "";
                zeroCounter = 0;
                x = "";
            }
        }
    }

    public String getInputStream() {
        return inputStream;
    }

    public void setInputStream(String inputStream) {
        this.inputStream = inputStream;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public ArrayList<HuffmanNode> getHuffmanTable() {
        return huffmanTable;
    }

    public int searchTable(HuffmanNode node) {
        for (int i = 0; i < huffmanTable.size(); i++) {
            if (huffmanTable.get(i).getTag().equals(node.getTag())) {
                return i;

            }
        }
        return -1;
    }

    public void generateHuffmanTable() {
        int probability = 0;
        for (int i = 0; i < tags.size(); i++) {
            for (int j = 0; j < tags.size(); j++) {
                if (tags.get(i).equals(tags.get(j))) {
                    probability++;
                }
            }
            HuffmanNode node = new HuffmanNode(tags.get(i), probability, "");
            if (searchTable(node) == -1) {
                huffmanTable.add(node);

            }
            probability = 0;


        }
    }

    private void createHuffmanTree() {
        {
            PriorityQueue<HuffmanNode> queue = new PriorityQueue<>(new MyComparator());
            for (int i = 0; i < huffmanTable.size(); i++) {
                queue.add(huffmanTable.get(i));
            }
            HuffmanNode root = null;
            while (queue.size() > 1) {
                HuffmanNode minimum = queue.peek();
                queue.poll();
                HuffmanNode secondMinimum = queue.peek();
                queue.poll();

                HuffmanNode sum = new HuffmanNode("-", minimum.getProbability() + secondMinimum.getProbability(), "");
                sum.setLeft(minimum);
                sum.setRight(secondMinimum);
                root = sum;
                queue.add(root);
            }
            huffmanTree = root;
        }
    }

    private void generateHuffmanCodes(HuffmanNode huffmanTree, String code) {
        if (huffmanTree.getLeft() == null && huffmanTree.getRight() == null && !huffmanTree.getTag().equals("-")) {

            huffmanTree.setCode(code);

            return;
        }
        generateHuffmanCodes(huffmanTree.getLeft(), code + "0");
        generateHuffmanCodes(huffmanTree.getRight(), code + "1");
    }

    public void HuffmanCompression() {
        createHuffmanTree();
        generateHuffmanCodes(huffmanTree, "");

    }

    private String onesComplement(String num) {

        num = num.replace('0', '2');
        num = num.replace('1', '3');
        num = num.replace('2', '1');
        num = num.replace('3', '0');
        return num;
    }

    public void generateCompressedStream() {
        ArrayList<String> extractedInputStream = new ArrayList<>();

        String x = "";
        for (int i = 0; i < inputStream.length(); i++) {
            x += inputStream.charAt(i);
            if (x.equals("-")) {
                continue;
            } else {

                if (!x.equals("0")) extractedInputStream.add(x);
                x = "";
            }
        }


        for (int j = 0; j < tags.size() - 1; j++) {
            HuffmanNode node = new HuffmanNode(tags.get(j), 0, "");
            int index = searchTable(node);


            if (extractedInputStream.get(j).charAt(0) == '-') {
                String bin = Integer.toBinaryString(Integer.parseInt(String.valueOf(extractedInputStream.get(j).charAt(1))));
                compressedStream += huffmanTable.get(index).getCode() + "" + onesComplement(bin) + "";
            } else {
                String bin = Integer.toBinaryString(Integer.parseInt(extractedInputStream.get(j)));
                compressedStream += huffmanTable.get(index).getCode() + "" + bin + "";
            }


        }

        compressedStream += "EOB";
    }

    private int searchTableByCode(String code) {
        for (int i = 0; i < huffmanTable.size(); i++) {
            if (huffmanTable.get(i).getCode().equals(code)) {
                return i;
            }
        }
        return -1;
    }

    public String decompress() {
        String code = "";
        String decompressedStream = "";
        String stream = compressedStream.substring(0, compressedStream.length() - 3);
        for (int i = 0; i < stream.length(); ) {
            code += stream.charAt(i);
            int index = searchTableByCode(code);
            if (index == -1) {
                i++;
                continue;
            } else {

                code = "";
                String tag = huffmanTable.get(index).getTag();
                String[] splittedTag = tag.split("/");
                for (int j = 0; j < Integer.parseInt(splittedTag[0]); j++) {
                    decompressedStream += "0";

                }
                String number = "";
                String bin = Integer.toBinaryString(Integer.parseInt(splittedTag[1]));
                i++;
                int x = i;
                for (int j = 0; j < bin.length(); j++) {
                    number += stream.charAt(i);
                    i++;
                }

                if (number.charAt(0) == '0') {

                    decompressedStream += String.valueOf(-1 * Integer.parseInt(onesComplement(number), 2));
                } else {

                    decompressedStream += String.valueOf((Integer) Integer.parseInt(number, 2));
                }


            }
        }
        int eobSize = inputStream.length() - decompressedStream.length();
        for (int j = 0; j < eobSize; j++) {
            decompressedStream += "0";
        }

        return decompressedStream;
    }

    public String getCompressedStream() {
        return compressedStream;
    }

    public void setCompressedStream(String compressedStream) {
        this.compressedStream = compressedStream;
    }
}




