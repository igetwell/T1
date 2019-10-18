package tech.getwell.t1.beans;

import java.util.LinkedList;
import java.util.Queue;

import tech.getwell.t1.utils.CommandManagement;

public class demo {

    public static void main(String[] args) {
        String data = "Wasftgww30323238";
        String n1 = String.valueOf(Integer.parseInt(data.toString().substring(8,10)) - 30);
        String n2 = String.valueOf(Integer.parseInt(data.toString().substring(10,12)) - 30);
        String n3 = String.valueOf(Integer.parseInt(data.toString().substring(12,14)) - 30);
        String n4 = String.valueOf(Integer.parseInt(data.toString().substring(14,16)) - 30);
        int version = Integer.parseInt(n1+n2+n3+n4);
        System.out.println("n1 = "+n1);
        System.out.println("n2 = "+n2);
        System.out.println("n3 = "+n1);
        System.out.println("n4 = "+n1);
        System.out.println("version = "+version);
        System.out.println("n = "+Integer.parseInt(data.toString().substring(8,16)));


        CommandManagement a = new CommandManagement();
        a.setListener(new tech.getwell.t1.utils.CommandManagement.OnCommandManagementListener() {
            @Override
            public void onCommandCallback(byte[] command) {
                System.out.println(" command = "+command);
            }
        });
        long time  = System.currentTimeMillis();
        a.pull(1);
        a.pull(1);
        a.pull(2);
        a.pull(10);
        a.pull(11);
        a.pull(999);
        System.out.println("时间:"+(System.currentTimeMillis() - time));

    }


    public static class CommandManagement{

        boolean isRunning;

        long time;

        Queue<Integer> queue = new LinkedList<>();

        tech.getwell.t1.utils.CommandManagement.OnCommandManagementListener listener;

        public void setListener(tech.getwell.t1.utils.CommandManagement.OnCommandManagementListener listener) {
            this.listener = listener;
        }

        void start(){
            if(queue.size() <= 0){
                isRunning = false;
                System.out.println("没有可执的命令");
                return;
            }
            if(isRunning){
                System.out.println("命令进行中..");
                return;
            }
            isRunning = true;
            while (isRunning){
                if(queue.size() <= 0){
                    isRunning = false;
                    System.out.println("执行完成...");
                    return;
                }
                //if(time <= 0)time = System.currentTimeMillis();
                long waitTime = System.currentTimeMillis() - time;
                if(waitTime <= 100){
                    try{
                        Thread.sleep(50);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    System.out.println("待执行中......"+50);
                    continue;
                }
                time = System.currentTimeMillis();
                byte[] command = getCommand(queue.poll());
                System.out.println("执行的命令 = "+command +" time = "+System.currentTimeMillis());
                if(listener != null){
                    listener.onCommandCallback(command);
                }
            }
            isRunning = false;
        }

        public void pull(Integer command){
            queue.offer(command);
            start();
        }

        public void close(){
            queue.clear();
            isRunning = false;
        }

        public static final int START_RUNNING = 1;

        public static final int START_RUNNING_DEBUG = 10;

        public static final int START_OTHER = 2;

        public static final int START_OTHER_DEBUG = 11;

        public static final int STOP = -1;

        public static final int VERSION = 999;

        /**
         * 获取设备指令
         * @param command
         * @return
         */
        public byte[] getCommand(int command){
            byte[] bytes = null;
            switch (command){
                // 停止接收数据
                case STOP:
                    bytes = new byte[]{0x73,(byte) 0xA0,0x01,(byte) 0xF0};
                    break;
                // 初始化数据 跑步类型
                case START_RUNNING:
                    bytes = new byte[]{0x73,(byte) 0xA0,0x01,0x01};
                    break;
                // 初始化数据 非跑步类型
                case START_OTHER:
                    bytes = new byte[]{0x73,(byte) 0xA0,0x01,0x02};
                    break;
                // 初始化数据 跑步类型 (调试模式,带原始数据)
                case START_RUNNING_DEBUG:
                    bytes = new byte[]{0x73,(byte) 0xA0,0x01,0x10};
                    break;
                // 初始化数据 非跑步类型 (调试模式,带原始数据)
                case START_OTHER_DEBUG:
                    bytes = new byte[]{0x73,(byte) 0xA0,0x01,0x11};
                    break;
                // 获取设备版本
                case VERSION:
                    bytes = new byte[]{0x73,0x6a,0x00,0x02};
                    break;
            }
            return bytes;
        }

        public interface OnCommandManagementListener{

            void onCommandCallback(byte[] command);
        }
    }


}
