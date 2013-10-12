/**
 * Copyright (C) 2013, Moss Computing Inc.
 *
 * This file is part of lucene-util.
 *
 * lucene-util is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * lucene-util is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with lucene-util; see the file COPYING.  If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
/**
 * 
 */
package com.moss.lucene.util;

import java.io.Reader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;

/**
 * An immutable wrapper around a lucene document.  The purpose of this class 
 * is to make it easier to write code that creates lucene documents.
 */
public class QuickDocWrap{
	private final QuickDocWrap parent;
	private String path;
	private final Document d;
	
	private final Store defaultStore;
	private final Index defaultIndex;
	
	private final String nullValue = "null";
	
	public QuickDocWrap(Document d) {
		super();
		this.d = d;
		this.path = "";
		this.parent = null;
		defaultStore = Store.NO;
		defaultIndex = Index.ANALYZED;
	}
	
	public QuickDocWrap(QuickDocWrap other, String path){
		this(other);
		this.path = this.path==null || this.path.length()==0?path:this.path + "." + path;
	}
	public QuickDocWrap(QuickDocWrap other){
		this(other, other.path, other.d, other.defaultStore, other.defaultIndex);
	}
	
	public QuickDocWrap(Document d, Store defaultStore, Index defaultIndex) {
		this(null, "", d, defaultStore, defaultIndex);
	}
	
	private QuickDocWrap(QuickDocWrap parent, String path, Document d, Store defaultStore, Index defaultIndex) {
		super();
		this.parent = parent;
		this.path = path;
		this.d = d;
		this.defaultStore = defaultStore;
		this.defaultIndex = defaultIndex;
	}

	public QuickDocWrap withDefaultIndex(Index defaultIndex) {
		return new QuickDocWrap(this, this.path, this.d, this.defaultStore, defaultIndex);
	}
	
	public QuickDocWrap withDefaultStore(Store defaultStore) {
		return new QuickDocWrap(this, this.path, this.d, defaultStore, this.defaultIndex);
	}
	
	public QuickDocWrap descend(String path){
		return new QuickDocWrap(this, path);
	}
	
	public QuickDocWrap parent() {
		return parent;
	}
	
	public Field field(String name, Reader reader){
		return add(new Field(name(name), reader));
	}
	
	public Field field(String name, TokenStream tokenStream){
		return add(new Field(name(name), tokenStream));
	}
	
	public Field field(String name, byte[] value, Store store){
		return add(new Field(name(name), value, store));
	}
	
	public Field field(String name, Reader reader, TermVector termVector){
		return add(new Field(name(name), reader, termVector));
	}
	
	public Field field(String name, TokenStream tokenStream, TermVector termVector){
		return add(new Field(name(name), tokenStream, termVector));
	}
	
	public Field field(String name, String value, Store store, Index index){
		return add(new Field(name(name), value==null?nullValue:value, store, index));
	}
	
	public Field field(String name, String value){
		return add(new Field(name(name), value==null?nullValue:value, defaultStore, defaultIndex));
	}
	
	private String name(String name){
		return path==null || path.length()==0?name:path + "." + name;
	}
	
	public Document document() {
		return d;
	}
	private <T extends Fieldable> T add(final T f){
		d.add(f);
		return f;
	}
}