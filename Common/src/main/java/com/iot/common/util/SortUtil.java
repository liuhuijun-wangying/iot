package com.iot.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * 排序工具类，包括选择、冒泡、插入、希尔排序
 * 和堆排序、快排、归并排序、基数排序
 * 
 */
public class SortUtil {

    public static void select(int[] array) {// O(n2)
        check(array);
        for (int i = 0; i < array.length; i++) {
            int minIndex = i;
            int min = array[i];
            for (int j = i + 1; j < array.length; j++) {// get min
                if (array[j] < min) {
                    min = array[j];
                    minIndex = j;
                }
            }
            if(minIndex!=i){
                swap(array,i,minIndex);
            }
        }
    }

    public static void insert(int[] array) {// O(n2)
        check(array);
        for (int i = 1; i < array.length; i++) {
            for (int j = i; j > 0; j--) {
                if(array[j-1]>array[j]){
                    swap(array,j-1,j);
                }else{
                    break;
                }
            }
        }
    }

    public static void shell(int[] array) {
        check(array);
        int d = array.length;
        while (true) {
            d = d / 2;
            for (int i = 0; i < array.length; i+=d) {
                for (int j = i; j-d>=0; j-=d) {
                    if(array[j-d]>array[j]){
                        swap(array,j-d,j);
                    }else{
                        break;
                    }
                }
            }

            if (d == 1) {
                break;
            }
        }
    }

	public static void bubble(int[] array) {// O(n2)
		for (int i = 0; i < array.length - 1; i++) {
			for (int j = i + 1; j < array.length; j++) {
				if (array[i] > array[j]) {
					swap(array,i,j);
				}
			}
		}
	}
	
	public static void heap(int[] arr){
		check(arr);
		for(int i=0;i<arr.length-1;i++){
			buildMaxHeap(arr,arr.length-i-1);
			swap(arr,0,arr.length-i-1);
		}
	}
	
	private static void buildMaxHeap(int[] arr, int lastIndex){
		for(int i=(lastIndex-1)/2;i>=0;i--){
			int biggerIndex = i;
			if(2*i+2<=lastIndex){//has left and right children
				biggerIndex = arr[2*i+1]>arr[2*i+2]?2*i+1:2*i+2;
			}else if(2*i+1<=lastIndex){//has left child only
				biggerIndex = 2*i+1;
			}
			
			if(biggerIndex!=i && arr[i]<arr[biggerIndex]){
				swap(arr,i,biggerIndex);
			}
		}
	}

    public static void quick(int[] array) {// O(nlgn)
        check(array);
        quick(array, 0, array.length - 1);
    }

    private static void quick(int[] array, int left, int right) {
        if (left < right) {
            int key = array[left]; // 数组的第一个作为中轴
            int low = left;
            int high = right;
            while (low < high) {
                while (low < high && array[high] >= key) {
                    high--;
                }
                array[low] = array[high]; // 比中轴小的记录移到低端
                while (low < high && array[low] <= key) {
                    low++;
                }
                array[high] = array[low]; // 比中轴大的记录移到高端
            }
            array[low] = key;
            quick(array, left, low - 1);
            quick(array, low + 1, right);
        }
    }

    public static void merge(int[] array) {// O(nlgn)
        check(array);
        merge(array, 0, array.length - 1);
    }

    private static void merge(int[] data, int left, int right) {
        if (left < right) {
            int center = (left + right) / 2;
            merge(data, left, center);
            merge(data, center + 1, right);
            doMerge(data, left, center, right);
        }
    }

    private static void doMerge(int[] data, int left, int center, int right) {
        int[] tmpArr = new int[data.length];
        int mid = center + 1;
        // third记录中间数组的索引
        int third = left;
        int tmp = left;
        while (left <= center && mid <= right) {
            // 从两个数组中取出最小的放入中间数组
            if (data[left] <= data[mid]) {
                tmpArr[third++] = data[left++];
            } else {
                tmpArr[third++] = data[mid++];
            }
        }
        // 剩余部分依次放入中间数组
        while (mid <= right) {
            tmpArr[third++] = data[mid++];
        }
        while (left <= center) {
            tmpArr[third++] = data[left++];
        }
        // 将中间数组中的内容复制回原数组
        while (tmp <= right) {
            data[tmp] = tmpArr[tmp++];
        }
    }

    public static void radix(int[] array) {
        //首先确定排序的趟数
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }

        int time = 0;
        // 判断位数;
        while (max > 0) {
            max /= 10;
            time++;
        }

        // 建立10个队列;
        List<ArrayList<Integer>> queue = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < 10; i++) {
            ArrayList<Integer> queue1 = new ArrayList<Integer>();
            queue.add(queue1);
        }

        // 进行time次分配和收集;
        for (int i = 0; i < time; i++) {
            // 分配数组元素;
            for (int j = 0; j < array.length; j++) {
                // 得到数字的第time+1位数;
                int x = array[j] % (int) Math.pow(10, i + 1)
                        / (int) Math.pow(10, i);
                ArrayList<Integer> queue2 = queue.get(x);
                queue2.add(array[j]);
                //queue.set(x, queue2);
            }

            int count = 0;// 元素计数器;
            // 收集队列元素;
            for (int k = 0; k < 10; k++) {
                while (queue.get(k).size() > 0) {
                    ArrayList<Integer> queue3 = queue.get(k);
                    array[count] = queue3.remove(0);
                    count++;
                }
            }
        }
    }

	//test
	public static void main(String[] args) {
		int[] arr = {54,65,432,765,43,57,7,3,89,43,7,100,200,9};
		heap(arr);
		System.out.println(Arrays.toString(arr));

        List<String> list = new ArrayList<String>();
        list.add("a");

        List<String> list2 = list;
        list.add("b");
        System.out.print(list2.size());

	}
	
	private static void check(int[] arr){
		if(arr==null || arr.length<2){
			throw new IllegalArgumentException();
		}
	}
	
	private static void swap(int[] arr, int i, int j){
		int tmp = arr[i];
		arr[i] = arr[j];
		arr[j] = tmp;
	}

}
