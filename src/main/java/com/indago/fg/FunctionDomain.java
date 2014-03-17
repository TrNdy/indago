package com.indago.fg;

import java.util.List;

public interface FunctionDomain {

	List< ? extends Domain< ? > > argumentDomains();
}
