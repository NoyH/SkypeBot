package org.kodas.skypebot.utils;

import com.google.gson.Gson;
import com.skype.Chat;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Noy on 09/05/2014.
 */
public final class Permissions {

    private static HashMap<String, HashMap<String, ArrayList<String>>> perms = new HashMap<>();
    private static File pfile = new File("Bot_Permissions.json");

    static boolean loaded = false;

    public static boolean hasPermission(User user, Chat chat, String perm) throws Exception{
        return hasPermission(user.getId(), chat.getId(), perm);
    }

    public static boolean hasPermission(String user, String chat, String perm) throws Exception{
        if(!loaded){
            load();
            loaded = true;
        } /* Only Accounts that have global permissions */
        return user.equals("n0yyyyyyyy") || user.equals("jordg.cjail") || user.equals("keith.altieri") || user.equals("owen.in") || (perms.containsKey("global") && perms.get("global").containsKey(user) && perms.get("global").get(user).contains(perm)) ||
                ( perms.containsKey(chat) && perms.get(chat).containsKey(user) && (perms.get(chat).get(user).contains(perm) ||  perms.get(chat).get(user).contains("*")));
    }

    public static void addPerm(User user, Chat chat, String perm) throws IOException{
        addPerm(user.getId(), chat.getId(), perm);
        save();
    }

    public static void addPerm(String user, String chat, String perm) throws IOException{
        getArray(user, chat).add(perm);
        save();
    }

    public static void removePerms(User user, Chat chat, String perm) throws IOException{
        removePerms(user.getId(), chat.getId(), perm);
        save();
    }

    public static void removePerms(String user, String chat, String perm) throws IOException{
        getArray(user, chat).remove(perm);
        save();
    }

    public static ArrayList<String> getArray(String user, String chat){
        HashMap<String, ArrayList<String>> map = perms.get(chat);
        if(map == null){
            map = new HashMap<>();
        }
        ArrayList<String> list = map.get(user);
        if(list == null){
            list = new ArrayList<>();
        }
        map.put(user, list);
        perms.put(chat, map);
        return list;
    }

    public static void save() throws IOException{
        Gson gson = new Gson();
        SkypePermission permissions = new SkypePermission();
        ArrayList<ChatPermission> chatperms = new ArrayList<ChatPermission>();
        for(String chat : perms.keySet()){
            ChatPermission chatperm = new ChatPermission();
            chatperm.chat = chat;
            HashMap<String, ArrayList<String>> map = perms.get(chat);
            ArrayList<UserPerm> userPerm = new ArrayList<UserPerm>();
            for(String str : map.keySet()){
                UserPerm user = new UserPerm();
                user.user = str;
                user.perms = map.get(str).toArray(new String[0]);
                userPerm.add(user);
            }
            chatperm.userPerm = userPerm.toArray(new UserPerm[0]);
            chatperms.add(chatperm);
        }
        permissions.chatPermission = chatperms.toArray(new ChatPermission[0]);


        String json = gson.toJson(permissions, SkypePermission.class);
        FileUtils.writeFile(pfile, json);
    }

    public static void load() throws Exception {
        String json = FileUtils.readFile(pfile);
        SkypePermission permissions = new Gson().fromJson(json, SkypePermission.class);

        HashMap<String, HashMap<String, ArrayList<String>>> newperm = new HashMap<>();

        if(permissions == null){
            perms = newperm;
            return;
        }

        for (ChatPermission chatperms : permissions.chatPermission){
            HashMap<String, ArrayList<String>> map = new HashMap<>();
            for (UserPerm userperms : chatperms.userPerm){
                 map.put(userperms.user, new ArrayList<>(Arrays.asList(userperms.perms)));
            }
            newperm.put(chatperms.chat, map);
        }
        perms = newperm;
    }

    public static Chat getChat(String id) throws SkypeException {
        Chat[] chats = Skype.getAllChats();
        for(Chat chat : chats){
            if(chat.getId().equals(id)){
                return chat;
            }
        }
        return null;
    }


    public final static class SkypePermission{
        ChatPermission[] chatPermission;
    }
    public final static class ChatPermission{
        String chat;
        UserPerm[] userPerm;
    }

    public final static class UserPerm {
        String user;
        String[] perms;
    }
}