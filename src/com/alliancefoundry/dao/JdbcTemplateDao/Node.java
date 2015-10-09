package com.alliancefoundry.dao.JdbcTemplateDao;

import java.util.ArrayList;
import java.util.List;

import com.alliancefoundry.model.Event;

/**
 * Created by: Bobby Writtenberry
 *
 */
public class Node {
	private Event event;
	private final List<Node> children = new ArrayList<Node>();
	private boolean inserted;
	
	/**
	 * @param event	stored in the node
	 */
	public Node(Event event){
		this.event = event;
	}
	
	/**
	 * @return the event
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * @return the children
	 */
	public List<Node> getChildren() {
		return children;
	}

	/**
	 * @return the inserted
	 */
	public boolean isInserted() {
		return inserted;
	}

	/**
	 * @param	event to be contained in the child node
	 * @param	top node of the tree being inserted in to
	 * @return	true if the child node was added, false if otherwise
	 */
	protected void insertNode(Event event, Node top){
		top.inserted = false;
		if(event.getParentId() != null && event.getParentId().equals(this.event.getEventId())){
			children.add(new Node(event));
			top.inserted = true;
			return;
		} else {
			for(Node n : this.children){
				n.insertNode(event, top);
			}
		}
	}
}
