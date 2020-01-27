package smell;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;



public class Assertion
{
	
	
	int total=0;
	
    public Assertion(String rootFileName)
    {
    	File rootFile = new File(rootFileName);
        browseClasses(rootFile);
    }

public void browseClasses(File rootFile) {
	
	

		StringBuilder sb = new StringBuilder();
		
    	new DirExplorer((level, path, file) -> path.endsWith(".java"),(level, path, file) ->  {
    		
        			String str = readUsingBufferedReaderCharArray(file);
        			
        			if(str.contains("junit"))
        			{
	        			ASTParser parser = ASTParser.newParser(AST.JLS3);
	    		parser.setSource(str.toCharArray());
	    		parser.setKind(ASTParser.K_COMPILATION_UNIT);
	    		
	    		final CompilationUnit c = (CompilationUnit) parser.createAST(null);
	    		
	    		c.accept(new ASTVisitor() {
	    			@Override
	    			public boolean visit(MethodDeclaration node) {
	    				
	    				ArrayList<Integer>lines=new ArrayList<Integer>();
	    				
	    				
	//    				if(node.getName().toString().contains("test")){
	    					
	    					node.accept(new ASTVisitor() {
	    						
	    						@Override
	    						public boolean visit(MethodInvocation node1) {
	    	
	    							if(node1.getName().toString().contains("assert"))
	    								{
	    								int li = c.getLineNumber(node1.getStartPosition());
	    								lines.add(li);
	    								
	    								}
	    							
	
	    								
	    								
	    								
	    							return super.visit(node1);
	    						}
	    					});
	//    				}
	    			
	    				
	    				
	    				if(lines.size()>1) {
							
	    					System.out.print("####Assertion Roulette Smell Detected##### ");
							
							total++;
							for(Integer line : lines){
								System.out.print(line+" ");
							}
							
							System.out.println("in "+ file.getName()+ "##" + "\n");
							}
	    				
	    				
	    				
	    				return super.visit(node);
	    			}
	    		});
	    		
	    		
	    		
	
	    		
	    		
	    		//Test test = new Test(cu,file.getName());
	    		//System.out.println(cu);
	    		//System.out.println(file.getName());
        			}
	    		
	        }).explore(rootFile);
	    	System.out.println(" Total error: " + total);
	    }
		
	

	
	
	
    private static String readUsingBufferedReaderCharArray(File file) {
		BufferedReader reader = null;
		StringBuilder stringBuilder = new StringBuilder();
		char[] buffer = new char[10];
		try {
			reader = new BufferedReader(new FileReader(file));
			while (reader.read(buffer) != -1) {
				stringBuilder.append(new String(buffer));
				buffer = new char[10];
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		return stringBuilder.toString();
	}
    
    
	
	int m_nStatementCount;
	public boolean visit(MethodDeclaration md) {
		// initialize the field to 0
		m_nStatementCount = 0;
		return true;
	}
	
	public void endVisit(MethodDeclaration md) {
		System.out.println("Statement count for method" + md.getName().getFullyQualifiedName() + 
		+ m_nStatementCount);
	 
	}
	 
	 
	// the visitors below increment the statement count field
	public boolean visit (AssertStatement node) {
		boolean flag=false;
		
		m_nStatementCount++;
		
		if (m_nStatementCount>0)
			flag= true;
		
		else if (m_nStatementCount==1|| m_nStatementCount<0)
			flag= false;
		return flag;
	
	}


}