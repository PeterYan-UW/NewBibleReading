package com.afc.biblereading.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.quickblox.users.model.QBUser;

public class Group {
	private String ID;
	private String name = null;
	private List<Integer> membersId = new ArrayList<>();
	private ArrayList<QBUser> membersQB = null;
	private HashMap<Integer, Member> members = null;
	private int finishRate = 0;
	
	public Group(String ID, String name, List<Integer> membersId) {
		super();
		this.ID = ID;
		this.name = name;
		for (Integer memberId : membersId){
			this.membersId.add(memberId);
		}
		this.members = new HashMap<Integer, Member>();
	}
	
	public void setGroupID(String groupID) {
		this.ID = groupID;
	}	
	public String getGroupID() {
		return this.ID;
	}
	
	public void setName(String name) {
		this.name = name;
	}	
	public String getName() {
		return this.name;
	}
	
	public void setMembersID(List<Integer> membersId) {
		this.membersId.addAll(membersId);
	}	
	public List<Integer> getMembersIdInteger() {
		return this.membersId;
	}
	public List<String> getMembersIdString() {
		List<String> membersStringId = new ArrayList<>();
		for (Integer id : this.membersId){
			membersStringId.add(String.valueOf(id));
		}
		return membersStringId;
	}
	public int getGroupSize() {
		if (this.membersId == null){
			return 0;
		}
		return this.membersId.size();
	}
	
	public void setFinishRate(int finishRate) {
		this.finishRate = finishRate;
	}	
	public int getFinishRate() {
		return this.finishRate;
	}
	
	public void addMember(Member member){
		this.members.put(member.getId(), member);
		if (members.size() == membersId.size()){
			notify();
		}
	}
	public ArrayList<Member> getMembers(){
		return (ArrayList<Member>) this.members.values();
	}
	public Member getMembersByID(int id){
		return this.members.get(id);
	}
	public void setMembersQB(ArrayList<QBUser> members) {
		this.membersQB = members;
	}
	public ArrayList<QBUser> getMembersQB() {
		return this.membersQB;
	}
}
