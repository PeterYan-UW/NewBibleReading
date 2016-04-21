package com.afc.biblereading.group;

import com.afc.biblereading.R;
import com.afc.biblereading.adapter.GroupListAdapter;
import com.afc.biblereading.helper.DataHolder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import static com.afc.biblereading.user.definitions.Consts.POSITION;

public class GroupListActivity extends Activity implements AdapterView.OnItemClickListener {
	
    private GroupListAdapter groupsListAdapter;
    private ListView groupList;
    private TextView titleTextView;
    private TextView joinGroupTextView;
    private TextView userGroupTextView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_list);
        initUI();
        initGroupList();
	}
	
	private void initUI() {
        groupList = (ListView) findViewById(R.id.groups_listview);
        titleTextView = (TextView) findViewById(R.id.group_page_content_textView);
        joinGroupTextView = (TextView) findViewById(R.id.join_group_page_content_textView);
        userGroupTextView = (TextView) findViewById(R.id.user_group_content_textView);
        titleTextView.setVisibility(View.INVISIBLE);
        joinGroupTextView.setVisibility(View.VISIBLE);
        userGroupTextView.setVisibility(View.INVISIBLE);
    }
    
    private void initGroupList() {
        if (DataHolder.getDataHolder().getGroupListSize() <= 0){
            joinGroupTextView.setText("暂时没有新的读经小组可以加入!");
        } else {
            joinGroupTextView.setText("请选择读经小组:");
            groupsListAdapter = new GroupListAdapter(this);
            groupList.setAdapter(groupsListAdapter);
            groupList.setOnItemClickListener(this);
        }
    }
    
	@Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        startShowGroupActivity(position);
    }

    private void startShowGroupActivity(int position) {
        Intent intent = new Intent(this, ShowGroupActivity.class);
        intent.putExtra(POSITION, position);
        startActivity(intent);
        if (DataHolder.getDataHolder().getSignInUserGroupListSize()>0){
        	finish();
        }
    }
}
