package com.indago.fg;

public interface AssignmentMapper< A, B >
{

	public Assignment< B > map( Assignment< ? super A > assignment );
}