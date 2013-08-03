package com.zehjot.smartday;

import org.json.JSONObject;

import com.zehjot.smartday.R;
import com.zehjot.smartday.TabListener.OnUpdateListener;
import com.zehjot.smartday.data_access.DataSet;
import com.zehjot.smartday.data_access.DataSet.onDataAvailableListener;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class SectionTimelineFragment extends Fragment implements OnUpdateListener,onDataAvailableListener{
	private JSONObject extra=null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		Log.d("Timeline", "CreateView");
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.section_timeline_fragment, container, false);
	}

	@Override
	public void onResume(){
		super.onResume();
		DataSet.getInstance(getActivity()).getApps((onDataAvailableListener) getActivity());	
	}
	
	public void onUpdate(JSONObject[] jObjs) {
		onDataAvailable(jObjs,"");
	}
	
	@Override
	public void putExtra(JSONObject jObj) {
		Activity act = getActivity();
		extra = jObj;
		if(act!=null){
			View tl = getActivity().findViewById(R.id.timelineview);
			if(tl!=null){
				((TimeLineView) tl).setExtra(jObj);
				extra=null;
			}				
		}
		
	}
	
	@Override
	public void onDataAvailable(JSONObject[] jObjs, String requestedFunction) {
		ViewGroup root = (ViewGroup) getActivity().findViewById(R.id.timelinell);			
		
		if(root!=null){
			
			
			
			if(getActivity().findViewById(R.id.timelineview)==null){
				LinearLayout linearLayout = new LinearLayout(getActivity());
				linearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				linearLayout.setOrientation(LinearLayout.VERTICAL);
				root.addView(linearLayout);
				TimeLineView timeline = new TimeLineView(getActivity());
				timeline.setId(R.id.timelineview);
				timeline.setLayoutParams(new LayoutParams(
						LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
				if(extra!=null){
					timeline.setExtra(extra);
					extra=null;
				}
				timeline.setData(jObjs[0]);
				linearLayout.addView(timeline);
			}else{				
				if(extra!=null){
					((TimeLineView)getActivity().findViewById(R.id.timelineview)).setExtra(extra);
					extra=null;
				}
				((TimeLineView)getActivity().findViewById(R.id.timelineview)).setData(jObjs[0]);
			}
			
			if(root.findViewById(101)==null){
				LinearLayout linearLayout = new LinearLayout(getActivity());
				linearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				linearLayout.setOrientation(LinearLayout.VERTICAL);
				root.addView(linearLayout);			
					TimeLineView timeline = new TimeLineView(getActivity());
					timeline.setLayoutParams(new LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					if(extra!=null){
						timeline.setExtra(extra);
						extra=null;
					}
					timeline.setData(jObjs[0]);
					timeline.setId(101);
					linearLayout.addView(timeline);
			}else{
				if(extra!=null){
					((TimeLineView)getActivity().findViewById(R.id.timelineview)).setExtra(extra);
					extra=null;
				}
				((TimeLineView)root.findViewById(101)).setData(jObjs[0]);
			}
			
			if(root.findViewById(102)==null){
				LinearLayout linearLayout = new LinearLayout(getActivity());
				linearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				linearLayout.setOrientation(LinearLayout.VERTICAL);
				root.addView(linearLayout);			
					TimeLineView timeline = new TimeLineView(getActivity());
					timeline.setLayoutParams(new LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
					if(extra!=null){
						timeline.setExtra(extra);
						extra=null;
					}
					timeline.setData(jObjs[0]);
					timeline.setId(102);
					linearLayout.addView(timeline);
			}else{
				if(extra!=null){
					((TimeLineView)getActivity().findViewById(R.id.timelineview)).setExtra(extra);
					extra=null;
				}
				((TimeLineView)root.findViewById(102)).setData(jObjs[0]);
			}
		}
	}
}
