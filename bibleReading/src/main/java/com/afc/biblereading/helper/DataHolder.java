package com.afc.biblereading.helper;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.afc.biblereading.group.Group;
import com.afc.biblereading.group.Member;
import com.quickblox.customobjects.model.QBCustomObject;
import com.quickblox.users.model.QBUser;

import static com.afc.biblereading.user.definitions.Consts.EMPTY_STRING;

public class DataHolder {
    private static DataHolder dataHolder;
    private List<QBUser> qbUsersList = new ArrayList<QBUser>();
    private List<QBCustomObject> qbGroupList = new ArrayList<QBCustomObject>();
    private List<Group> groupList = new ArrayList<Group>();
    private ArrayList<String> userLogList = new ArrayList<String>();
    private ArrayList<Member> Member = new ArrayList<Member>();
    private ArrayList<QBCustomObject> signInUserQBGroup = new ArrayList<QBCustomObject>();
    private QBUser signInQbUser;
    private ArrayList<Group> signInUserGroup = new ArrayList<Group>();

    public static synchronized DataHolder getDataHolder() {
        if (dataHolder == null) {
            dataHolder = new DataHolder();
        }
        return dataHolder;
    }
    public void setUserLogList(ArrayList<String> userLogList){
    	this.userLogList = userLogList;
    }
    public ArrayList<String> getUserLogList(){
    	return this.userLogList;
    }
    public void setQBGroupList(List<QBCustomObject> qbGroupList){
    	Log.v("data Holder", "set group list");
    	this.qbGroupList = qbGroupList;
    }
    public QBCustomObject getQBGroup(int index){
    	return this.qbGroupList.get(index);
    }
    public void setGroupList(List<Group> GroupList) {
        this.groupList = GroupList;
    }
    public int getGroupListSize() {
        return groupList.size();
    }
	public Group getGroup(int index) {
		return groupList.get(index);
	}
	public String getGroupName(int index) {
        return groupList.get(index).getName();
	}
	public int getGroupRate(int index) {
		return groupList.get(index).getFinishRate();
	}
	
    public void setQbUsersList(List<QBUser> qbUsersList) {
        this.qbUsersList = qbUsersList;
    }

    public int getQBUserListSize() {
        return qbUsersList.size();
    }

    public String getQBUserName(int index) {
        return qbUsersList.get(index).getFullName();
    }

    public List<String> getQbUserTags(int index) {
        return qbUsersList.get(index).getTags();
    }

    public QBUser getQBUser(int index) {
        return qbUsersList.get(index);
    }

    public QBUser getLastQBUser() {
        return qbUsersList.get(qbUsersList.size() - 1);
    }

    public void addQbUserToList(QBUser qbUser) {
        qbUsersList.add(qbUser);
    }

    public QBUser getSignInQbUser() {
        return signInQbUser;
    }

    public void setSignInQbUser(QBUser singInQbUser) {
        this.signInQbUser = singInQbUser;
    }   
    
    public int getSignInUserGroupListSize() {
        return signInUserGroup.size();
    }
    public Group getSignInUserGroup(int index) {
        return signInUserGroup.get(index);
    } 
    public void setSignInUserGroup(ArrayList<Group> groups) {
    	Log.v("data holder", "set user groups");
    	if (groups != null){
    		this.signInUserGroup = new ArrayList<Group>();
            this.signInUserGroup.addAll(groups);    		
    	}
    }
    public void addSignInUserGroup(Group group) {
    	Log.v("data holder", "add user group");
        this.signInUserGroup.add(group);
    }
    public void removeSignInUserGroup(String groupId) {
        Log.v("data holder", "remove user group");
        for (Group group : this.signInUserGroup){
            if (group != null) {
                if (group.getGroupID() != null) {
                    if(group.getGroupID().equals(groupId)){
                        this.signInUserGroup.remove(group);
                        return;
                    }
                }else {
                    this.signInUserGroup.remove(group);
                }
            }
        }
    }
    public QBCustomObject getSignInUserQbGroup(int index) {
		return signInUserQBGroup.get(index);
    } 
    public void setSignInUserQbGroup(ArrayList<QBCustomObject> groups) {
    	Log.v("data holder", "set user groups");
    	if (groups != null){
    		this.signInUserQBGroup = new ArrayList<QBCustomObject>();
    		this.signInUserQBGroup.addAll(groups);
    	}
    }
    public void addSignInUserQbGroup(QBCustomObject group) {
    	Log.v("data holder", "add user group");
        this.signInUserQBGroup.add(group);
    }
    public void removeSignInUserQbGroup(String group) {
        Log.v("data holder", "set user groups");
        for (QBCustomObject QBgroup : this.signInUserQBGroup){
            if (((String)(QBgroup.getFields().get("id"))) != null){
                this.signInUserQBGroup.remove(group);
                if (((String)(QBgroup.getFields().get("id"))).equals(group)){
                    this.signInUserQBGroup.remove(group);
                    return;
                }
            }else {
                this.signInUserQBGroup.remove(group);
            }
        }
    }

    public String getSignInUserOldPassword() {
        return signInQbUser.getOldPassword();
    }

    public int getSignInUserId() {
    	return signInQbUser.getId();
    }

    public void setSignInUserPassword(String singInUserPassword) {
        signInQbUser.setOldPassword(singInUserPassword);
    }

    public String getSignInUserLogin() {
        return signInQbUser.getLogin();
    }

    public String getSignInUserEmail() {
        return signInQbUser.getEmail();
    }

    public String getSignInUserFullName() {
        return signInQbUser.getFullName();
    }

    public String getSignInUserPhone() {
        return signInQbUser.getPhone();
    }

    public String getSignInUserWebSite() {
        return signInQbUser.getWebsite();
    }

    public String getSignInUserTags() {
        if (signInQbUser.getTags() != null) {
            return signInQbUser.getTags().getItemsAsString();
        } else {
            return EMPTY_STRING;
        }
    }
}
