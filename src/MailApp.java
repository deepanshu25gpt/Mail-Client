
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.sql.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.net.URL;
import javax.swing.table.*;
import javax.swing.border.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
class MailApp implements ActionListener,TreeSelectionListener,ListSelectionListener,TableModelListener,MouseListener
{
	private ConfigUtility configUtil = new ConfigUtility();
	private JFrame create,f;
	private JButton compose,recieve,selectall,restore,restoreall,delete,deleteall,reply,forward,send,save,cancel;
	private JPanel jp1,jp2,jp3,jp4;
	private JSplitPane sp1;
	private JTextField from,to,subject;
	private JTextArea ta;
	private JLabel frm,t,sub,m;
	private JTree tree;
	private DefaultTableModel model;
	private JTable table;
	private int tableflag=0;
	private JScrollPane pane;
	private String table_name,username,password;
	Connection con;
	Statement stmt;
	public MailApp()
	{
		f=new JFrame("Mail Application"); 
		JMenuBar mb=new JMenuBar();
		JMenu setting=new JMenu("Settings");
		JMenuItem opt=new JMenuItem("Options");
		
		opt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				SettingsDialog dialog = new SettingsDialog(f, configUtil);
				dialog.setVisible(true);
			}
		});
		setting.add(opt);
		
		mb.add(setting);
		f.setJMenuBar(mb);
		setting.addActionListener(this);
        jp1=new JPanel();
        f.add(jp1,"North");
        jp2=new JPanel();
        f.add(jp2,"West");
        jp3=new JPanel();
        f.add(jp3,"Center");           
        jp4=new JPanel();
        jp1.add(jp4);
        jp4.setLayout(new GridLayout(2,1));
        sp1=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,true,jp2,jp3);
        f.add(sp1);
        compose=new JButton("Compose");
        jp4.add(compose);
        compose.addActionListener(this);
        recieve=new JButton("Recieve");
        jp4.add(recieve);
        recieve.addActionListener(this); 
        jp1.setLayout(new FlowLayout());
        selectall=new JButton("Select All");
        jp1.add(selectall);
        selectall.addActionListener(this);
        restore=new JButton("Restore");
        jp1.add(restore);
        restore.addActionListener(this);
        restoreall=new JButton("Restore All");
        jp1.add(restoreall);
        restoreall.addActionListener(this);
        delete=new JButton("Delete");
        jp1.add(delete);
        delete.addActionListener(this);
        deleteall=new JButton("Delete All");
        jp1.add(deleteall);
        deleteall.addActionListener(this);
        reply=new JButton("Reply");
        jp1.add(reply);
        reply.addActionListener(this);
        forward=new JButton("Forward");
        jp1.add(forward);
        forward.addActionListener(this); 
        optionsTree();
          selectall.setVisible(false);
    	  restore.setVisible(false);
    	  restoreall.setVisible(false);
    	  delete.setVisible(false);
    	  deleteall.setVisible(false);
    	  reply.setVisible(false);
    	  forward.setVisible(false);
          f.setSize(1000,1000);
          f.setVisible(true);
          /***********************************************************************************************/
          try
          {
             Class.forName("com.mysql.jdbc.Driver");
		     String url="jdbc:mysql://localhost:3306/mail?user=root&password=root";
		     con=DriverManager.getConnection(url);
		     stmt=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
          }
          catch(Exception s1)
          {
          	s1.printStackTrace();
          }
          /***********************************************************************************************/
	}
	class MyDefaultTableModel extends DefaultTableModel
	{
	    MyDefaultTableModel(Object[][] data, Object[] columnNames) 
	    {
	    	super(data,columnNames);
	    }
	    public boolean isCellEditable(int row, int column) 
	    {
	        if(column<1)
	        return true;
	        else return false;
	    }
	}
	public void valueChanged(ListSelectionEvent e)
    {
    	System.out.println("value changed");
		if(e.getValueIsAdjusting() == false)
		{		
			Object val1=null,val2=null,val3=null;
			int row = table.getSelectedRow();
			if (row > -1)
			{
				val1=model.getValueAt(row,1);
			    val2=model.getValueAt(row,2);
			    val3=model.getValueAt(row,3);	
			//	System.out.println("val="+val1+"*******"+val2+"******"+val3);
			}	
		}
    }
    public void mouseClicked(MouseEvent em)
    {
        if (em.getClickCount() == 2)
        {
            int index = table.rowAtPoint(em.getPoint());
            Object from = model.getValueAt(index,1);
            Object subject = model.getValueAt(index,2);
            Object body = model.getValueAt(index,3);
            showMailGUI((String)from,(String)subject,(String)body,"inbox");
        }
    }
    public void mouseReleased(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void optionsTree() 
    {
    	jp2.setLayout(new BorderLayout());
    	DefaultMutableTreeNode mailboxes = new DefaultMutableTreeNode("MailBoxes");
    	DefaultMutableTreeNode inbox = new DefaultMutableTreeNode("Inbox");
    	DefaultMutableTreeNode sentbox = new DefaultMutableTreeNode("SentBox");
    	DefaultMutableTreeNode outbox = new DefaultMutableTreeNode("OutBox");
    	DefaultMutableTreeNode drafts = new DefaultMutableTreeNode("Drafts");
        DefaultMutableTreeNode trash = new DefaultMutableTreeNode("Trash");
        mailboxes.add(inbox);
        mailboxes.add(sentbox);
        mailboxes.add(outbox);
        mailboxes.add(drafts);
        mailboxes.add(trash);
    	tree = new JTree(mailboxes);
    	tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    	tree.addTreeSelectionListener(this);
    	
    	DefaultTreeCellRenderer render = new DefaultTreeCellRenderer();
    	render.setLeafIcon(createImageIcon("Icon/iphone.GIF"));
    	render.setClosedIcon(createImageIcon("Icon/iphone.GIF"));
    	render.setOpenIcon(createImageIcon("Icon/iphone.GIF"));
    	tree.setCellRenderer(render);
    	
    	JScrollPane treeView = new JScrollPane(tree);
    	treeView.setPreferredSize(new Dimension(180,800));
    	jp2.add(treeView);
    }
    public void tableChanged(TableModelEvent e)
    	 {
    	 	System.out.println("IN TABLECHANGED METHOD:");
                int row = e.getFirstRow();
                int column = e.getColumn();
             if (column == 0) 
          	{
               String columnName = model.getColumnName(column);
               Boolean checked = (Boolean) model.getValueAt(row, column);
               if (checked) 
               	{
                System.out.println(columnName + ": " + true);
                }
                else 
                {
                 System.out.println(columnName + ": " + false);
                }
           }
        }
    public void valueChanged(TreeSelectionEvent e) {

    	DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
    	System.out.println(node.getUserObject());
    	if(node.getUserObject().equals("Inbox"))
    	{
    	  selectall.setVisible(true);
    	  restore.setVisible(false);
    	  restoreall.setVisible(false);
    	  delete.setVisible(true);
    	  deleteall.setVisible(true);
    	  reply.setVisible(true);
    	  forward.setVisible(true);
    	  table_name="inbox";
    	  tablemaker("inbox");
    	}
    	else if(node.getUserObject().equals("SentBox"))
    	{
    	  selectall.setVisible(true);
    	  restore.setVisible(false);
    	  restoreall.setVisible(false);
    	  delete.setVisible(true);
    	  deleteall.setVisible(true);
    	  reply.setVisible(false);
    	  forward.setVisible(true);
    	  table_name="sentbox";
    	  tablemaker("sentbox");
    	}
    	else if(node.getUserObject().equals("OutBox"))
    	{
    	  selectall.setVisible(true);
    	  restore.setVisible(false);
    	  restoreall.setVisible(false);
    	  delete.setVisible(true);
    	  deleteall.setVisible(true);
    	  reply.setVisible(false);
    	  forward.setVisible(true);
    	   table_name="outbox";
    	  tablemaker("outbox");
    	}
    	else if(node.getUserObject().equals("Drafts"))
    	{
    	  selectall.setVisible(true);
    	  restore.setVisible(false);
    	  restoreall.setVisible(false);
    	  delete.setVisible(true);
    	  deleteall.setVisible(true);
    	  reply.setVisible(false);
    	  forward.setVisible(false);
    	   table_name="draft";
    	  tablemaker("draft");
    	}
    	else if(node.getUserObject().equals("Trash"))
    	{ 	  	 
     	  selectall.setVisible(true);
    	  restore.setVisible(true);
    	  restoreall.setVisible(true);
    	  delete.setVisible(true);
    	  deleteall.setVisible(true);
    	  reply.setVisible(false);
    	  forward.setVisible(false);
    	  table_name="trash";
    	  tablemaker("trash");
    	}	
    }
    public void tablemaker(String table_name)
    {
    	if(tableflag==1)	
    	  {
    	  	jp3.remove(pane);
    	  	tableflag=0;
    	  }
    	try
    	  {
			     ResultSet rs=stmt.executeQuery("select count(*) as total from "+table_name);
			     rs.next();
			     int rcount=rs.getInt("total");
			     Object rows[][]=new Object[rcount][4];
			     ResultSet rs1=stmt.executeQuery("select * from "+table_name); 
			     String first_column="";
			     if(table_name=="trash")
			     first_column="from/to";
			     if(table_name=="inbox")
			     first_column="from";
			     if(table_name=="outbox")
			     first_column="to";
                 if(table_name=="draft")
			     first_column="to";  
			     if(table_name=="sentbox")
			     first_column="to";  
			     int i=0;
			   	 while(rs1.next())
			     {
			     	rows[i][0]=false;
			     	rows[i][1]=rs1.getString(first_column);
				    rows[i][2]=rs1.getString("subject");
				    rows[i][3]=rs1.getString("body");
				    i++;
			     }
			String columns[] = {"mark",first_column,"Subject","Body"};
	    	model = new MyDefaultTableModel(rows,columns);
	    	table = new JTable(model)
	    	{
	    	   //------------------
	    	   @Override
               public Class getColumnClass(int column)
               {
                 return column == 0 ? Boolean.class : String.class;
               }
              
    	   };
    	     table.getModel().addTableModelListener(this);
    	     table.getSelectionModel().addListSelectionListener(this);
	    //---------------------------------	
	    	tableflag=1;
	     	pane = new JScrollPane(table);
	     	table.getTableHeader().setReorderingAllowed(false);
	     	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
            table.addMouseListener(this);
	        jp3.add(pane);
		    jp3.revalidate();		           
    	  }
    	  catch(Exception a)
    	  {
    	  	tableflag=0;
    	  	a.printStackTrace();
    	  }
    	  
    }
    private ImageIcon createImageIcon(String path) {
    	
    	URL imageURL = this.getClass().getResource(path);
    	if(imageURL != null)
    		return new ImageIcon(imageURL);
    	else
    		return null;
    }
	//--------------
	public void loadID()
	{
        try{
    		    Properties smtpProperties = configUtil.loadProperties();
                username = smtpProperties.getProperty("mail.user");
		        password = smtpProperties.getProperty("mail.password");
		System.out.println("username="+username+"  password="+password);
    	
    	}
    	catch(Exception p1)
    	{
    		p1.printStackTrace();
    	}
		
	}
	 public void showMailGUI(String no1,String no2,String no3,String type)
    {
    	    create=new JFrame();
			create.setSize(300,500);
			create.setLayout(new GridBagLayout());
			create.setResizable(true);
			frm=new JLabel("From:");
			t=new JLabel("To:");
			sub=new JLabel("Subject:");
			m=new JLabel("Message:");
			from=new JTextField(20);
            to=new JTextField(20);
			subject=new JTextField(20);
			ta=new JTextArea(20,20);
			JScrollPane jsp=new JScrollPane(ta);
			send=new JButton("Send");
			save=new JButton("Save");
			cancel=new JButton("Cancel");
			GridBagConstraints constraints = new GridBagConstraints();
			/******************************************************************************************/
			 if(  type.equalsIgnoreCase("inbox")  )
    	    {
    	       constraints.gridx = 0;
		       constraints.gridy = 0;
		       create.add(frm,constraints);
		       constraints.gridx = 1;
		       create.add(from,constraints);
     	       from.setText(no1);
     	       subject.setText(no2);
    	       ta.setText(no3);
    	       from.setEditable(false);
    	       subject.setEditable(false);
    	       ta.setEditable(false);
    	       send.setVisible(false);
    	       save.setVisible(false);
    	       cancel.setVisible(false);
    	    }
    	    else if(type.equalsIgnoreCase("reply"))
    	    {
    	    	to.setText(no1);
    	    }
    	    else if(type.equalsIgnoreCase("forward"))
    	    {
    	    	subject.setText(no2);
    	    	ta.setText(no3);
    	    }
			/******************************************************************************************/
     	    /******************************************************************************************/
     	    if(  !(type.equalsIgnoreCase("inbox")) )
     	    {
     	    	 constraints.gridx = 0;
		         constraints.gridy = 1;
		         create.add(t,constraints);
		         constraints.gridx = 1;
		         create.add(to,constraints);
     	    }
		    /**************************************************************************************************/
		    constraints.gridx = 0;
		    constraints.gridy = 2;
		    create.add(sub,constraints);
		    constraints.gridx = 1;
		    create.add(subject,constraints);
		    constraints.gridx = 0;
		    constraints.gridy = 3;
		    constraints.anchor=GridBagConstraints.NORTHEAST;
		    create.add(m,constraints);
		    constraints.gridx = 1;
		    create.add(jsp,constraints);
		    constraints.gridx = 0;
		    constraints.gridy = 4;
		    constraints.anchor=GridBagConstraints.CENTER;
		    send.setFont(new Font("Arial", Font.BOLD, 16));
		    create.add(send,constraints);
		    send.addActionListener(new ActionListener()
		    {
		    	public void actionPerformed(ActionEvent s)
		    	{
		    		loadID();
		    		Message msg;
                    Properties props = new Properties();
		            props.put("mail.smtp.host","smtp.gmail.com");
		            props.put("mail.smtp.port", "465"); // default port 25
	             	props.put("mail.smtp.auth","true"); 
	             	props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		            props.put("mail.debug", "false");
                    Session session = Session.getDefaultInstance(props,new SimpleMailAuthenticator(username,password));
                    try
		           {
		           	  //---------------
		           	  
			          System.out.println("***********----------------*****************");
			          System.out.println(to.getText()+"***"+subject.getText()+"****"+ta.getText());
		           	  //--------------
			          msg=new MimeMessage(session);
			          msg.setFrom(new InternetAddress(username));
			          msg.setRecipient(Message.RecipientType.TO,new InternetAddress(to.getText()));
			          msg.setSubject(sub.getText());
 			          msg.setText(ta.getText());
                      /**********************************************************/
                      		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
           	  	            Date dateobj = new Date();
           	  	            String date1=df.format(dateobj);
           	  	            Timestamp dda1=Timestamp.valueOf(date1);
			          /***********************************************************/
			          Transport.send(msg);
			          /***********************************************************/
			          ResultSet rs=stmt.executeQuery("select count(*) as total from sentbox");
			          rs.next();
			          int rcount=rs.getInt("total");
			          /***************************************************************/
			          stmt.executeUpdate("insert into sentbox values('"+(rcount+1)+"','"+to.getText()+"','"+subject.getText()+"','"+ta.getText()+"','"+dda1+"')");
			          create.setVisible(false);
			          JOptionPane.showMessageDialog(create,"send sucessfully!!","error",JOptionPane.INFORMATION_MESSAGE);    
	               }
		           catch(Exception me)
		           {
			         me.printStackTrace();
			         JOptionPane.showMessageDialog(create,me.getMessage(),"error",JOptionPane.INFORMATION_MESSAGE);
			         try
			         {
			          System.out.println("IN OUTBOX");
			          DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
           	  	      Date dateobj = new Date();
           	  	      String date1=df.format(dateobj);
           	  	      Timestamp dda1=Timestamp.valueOf(date1);
           	  	       /***********************************************************/
			          ResultSet rs=stmt.executeQuery("select count(*) as total from outbox");
			          rs.next();
			          int rcount=rs.getInt("total");
			          /***************************************************************/
			          stmt.executeUpdate("insert into outbox values('"+(rcount+1)+"','"+to.getText()+"','"+subject.getText()+"','"+ta.getText()+"','"+dda1+"')");
			          create.setVisible(false);
			         }
			         catch(Exception obox)
			         {
			         	obox.printStackTrace();
			         }
		           }
		    	}
		    });
		    constraints.gridx = 1;
		    constraints.gridy = 4;
		    save.setFont(new Font("Arial", Font.BOLD, 16));
		    create.add(save,constraints);
		    save.addActionListener(new ActionListener(){
		    	public void actionPerformed(ActionEvent sve)
		    	{
		    		try
		    		{
		    			/**********************************************************/
		    			    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
           	  	            Date dateobj = new Date();
           	  	            String date1=df.format(dateobj);
           	  	            Timestamp dda1=Timestamp.valueOf(date1);
			          /***********************************************************/
			          /***********************************************************/
			          ResultSet rs=stmt.executeQuery("select count(*) as total from draft");
			          rs.next();
			          int rcount=rs.getInt("total");
			          /***************************************************************/
			          stmt.executeUpdate("insert into draft values('"+(rcount+1)+"','"+to.getText()+"','"+subject.getText()+"','"+ta.getText()+"','"+dda1+"')");
			          create.setVisible(false);
			          JOptionPane.showMessageDialog(create,"saved sucessfully!!","error",JOptionPane.INFORMATION_MESSAGE);
		    		}
		    		catch(SQLException sve1)
		    		{
		    			JOptionPane.showMessageDialog(f,sve1.getMessage(),"error",JOptionPane.INFORMATION_MESSAGE);
		    			sve1.printStackTrace();
		    		}
		    		  
                     
		    	}
		    });
		    constraints.gridx = 2;
		    constraints.gridy = 4;
		    cancel.setFont(new Font("Arial", Font.BOLD, 16));
		    create.add(cancel,constraints);
		    cancel.addActionListener(new ActionListener()
		    {
		    	public void actionPerformed(ActionEvent c)
		    	{
		    		from.setText("");
		    		to.setText("");
		    		subject.setText("");
		    		ta.setText("");
		    	}
		    });
		    create.pack();
		    create.setLocationRelativeTo(null);
		    create.setResizable(false);
			create.setVisible(true);
    }	
	public void actionPerformed(ActionEvent e)
	{
		String str=e.getActionCommand();
		System.out.println(str+" is clicked");
		if(str.equals("Compose"))
		{
			showMailGUI("","","","compose");
		}
		else if(str.equals("Recieve"))
		{
		    try
		    {
		    	loadID();
			Properties props = new Properties();
			props.put("mail.imap.host", "imap.gmail.com");
			props.put("mail.imap.port", "993");// default port 143
		    props.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
			Session session = Session.getDefaultInstance(props,null);
			Store store = session.getStore("imap");
			store.connect(username,password);
			Folder inboxFolder = store.getFolder("inbox");
			inboxFolder.open(Folder.READ_ONLY);
			Message[] arr = inboxFolder.getMessages();
			ResultSet rs = stmt.executeQuery("select * from inbox order by date");
			boolean rscheck=true;
			System.out.println("first="+rs.first());	
			if(rs.first()==false)
			rscheck=false;
			else
			rs.beforeFirst();		
		    for(int i=0; i<arr.length ;i++)
			{
			    Address[] from=arr[i].getFrom();
			    Object body=arr[i].getContent();
                String msg="";
                if(body instanceof String)
                {
                    msg=(String)body;
                }
                else
                {
                    MimeMultipart m=(MimeMultipart)body;
                    BodyPart bp;
                    for(int k=0;k<m.getCount();k++)
                    {
                        bp=m.getBodyPart(k);
                        if(bp.isMimeType("text/plain"))
                        {
                            msg=(String)bp.getContent();
                            msg=msg.replace("'",",,");
                            break;
                        }
                    }
                }
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
           	  	Date dateobj = arr[i].getSentDate();
           	  	String date1=df.format(dateobj);
           	  	Timestamp dda1=Timestamp.valueOf(date1);
			    System.out.println("DATE="+date1);
			    boolean set=true;
			    
			    if(rscheck)
			    if(rs.next())
			    {
			    	System.out.println(rs.getTimestamp("Date"));
			    	System.out.println(dda1);
			    	  if(rs.getTimestamp("Date").equals(dda1))
			    	  {
			    	  	 set=false;
			    	  	 System.out.println("set="+set);
			    	  }
			    	  if(rs.isLast())
			    	  {
			    	  	System.out.println("is last"+rs.isLast());
			    	  	rscheck=false;
			    	  }	  	
			    }  
			    if(set)
			    {
			      stmt.executeUpdate("INSERT into inbox values('"+(i+1)+"','"+from[0]+"','"+arr[i].getSubject()+"','"+msg+"','"+dda1+"')");
			      System.out.println("Sucessfully inserted i think");	
			    }   		
			}
			tablemaker("inbox");	
		    }
		    catch(Exception n)
		    {
		    	JOptionPane.showMessageDialog(f,n.getMessage(),"error",JOptionPane.INFORMATION_MESSAGE);
		    	n.printStackTrace();
		    }
		    
		}
		else if(str.equals("Select All"))
		{
			for(int i=0;i<table.getRowCount();i++)
			{
				Boolean checked = (Boolean) model.getValueAt(i,0);
				if(checked==false)
				table.getModel().setValueAt(true, i,0);
				else
				table.getModel().setValueAt(false, i,0);
			}
            
		}
		else if(str.equals("Restore"))
		{
			    reStore();
		}
		else if(str.equals("Restore All"))
		{
					for(int i=0;i<table.getRowCount();i++)
                    table.getModel().setValueAt(true, i,0);
                    reStore();             
		}
		else if(str.equals("Delete"))
		{
			   deleteRow();  
		}
		else if(str.equals("Delete All"))
		{
			try
				{ 
			    	if(!(table_name.equals("trash")))
			    	{
			    		ResultSet rs2=stmt.executeQuery("select count(*) as total from trash");
			            rs2.next();
			            int rcount=rs2.getInt("total");
			            
			    		for(int i=0;i<table.getRowCount();i++)
                        table.getModel().setValueAt(true, i,0);
			    		
			    		String column="to";
			    		if(table_name.equals("inbox"))
			    		column="from";
			    		
			    		for(int k=0;k<table.getRowCount();k++)
			    		{
			    			 System.out.println("select * from "+table_name+" where id"+table_name+"='"+(k+1)+"'");
			    			 ResultSet rs=stmt.executeQuery("select * from "+table_name+" where id"+table_name+"='"+(k+1)+"'");
			    			 rs.next();
			    			 ++rcount;
			    			 stmt.executeUpdate("INSERT into trash values('"+(rcount)+"','"+rs.getString(column)+"','"+rs.getString("subject")+"','"+rs.getString("body")+"','"+rs.getString("date")+"','"+table_name+"')");
			    		}    		
			    	}
			        stmt.executeUpdate("delete from "+table_name);
			    }
			    catch(Exception dall)
			    {
			    	JOptionPane.showMessageDialog(f,dall.getMessage(),"error",JOptionPane.INFORMATION_MESSAGE);
			    	dall.printStackTrace();
			    }
			    tablemaker(table_name);
			     
		}
		else if(str.equals("Reply"))
		{
			sendData("reply");		     
		}
		else if(str.equals("Forward"))
		{
			sendData("forward");
		}
		else if(str.equals("Options"))
		{
			
		}
	}
	public void sendData(String type)
	{
		int count=0,k;
			Object val1=null,val2=null,val3=null;
			for(int i=0;i<table.getRowCount();i++)
			     {
			      Boolean checked = (Boolean) model.getValueAt(i,0);
			         if(checked==true)
			         {
			      	    val1=model.getValueAt(i,1);
			      	    val2=model.getValueAt(i,2);
			      	    val3=model.getValueAt(i,3);
			      	    table.getModel().setValueAt(false,i,0);
			      	    count++;
			         } 	
			     }
			     if(count==1)
			     {
			     	showMailGUI((String)val1,(String)val2,(String)val3,type);
			     }
			     else
			     JOptionPane.showMessageDialog(f,"Invalid Selection(Select one mail only)","error",JOptionPane.INFORMATION_MESSAGE);
	}	
	public void deleteRow()
	{
		 try
			{
	              for(int i=0;i<table.getRowCount();i++)
			     {
			      Boolean checked = (Boolean) model.getValueAt(i,0);
			      if(checked==true)
			      {
			      	Object val1=model.getValueAt(i,1);
			      	Object val2=model.getValueAt(i,2);
			      	Object val3=model.getValueAt(i,3);
			      	System.out.println("i="+i);
			      	System.out.println("select * from "+table_name+" where id"+table_name+"='"+(i+1)+"'");
			        ResultSet rs1=stmt.executeQuery("select * from "+table_name+" where id"+table_name+"='"+(i+1)+"'");
			        rs1.next();
			      	Object val4=rs1.getString("date");
			      	System.out.println((String)val4);
			      	System.out.println("delete from "+table_name+" where id"+table_name+"='"+(i+1)+"'");
			      	stmt.executeUpdate("delete from "+table_name+" where id"+table_name+"='"+(i+1)+"'"); 
			      	System.out.println(table_name);
			      	if(!(table_name.equals("trash")))
			      	{
			      	  System.out.println("IN TRASH UPDATE "); 
			      	  /***********************************************************/
			          ResultSet rs=stmt.executeQuery("select count(*) as total from trash");
			          rs.next();
			          int rcount=rs.getInt("total");
			          /***************************************************************/	
			          stmt.executeUpdate("insert into trash values('"+(rcount+1)+"','"+(String)val1+"','"+(String)val2+"','"+(String)val3+"','"+(String)val4+"','"+table_name+"')");
			      	}
			      	checked=false;
			      }
			     } 
			     	 tablemaker(table_name);
			       /***********************************************************/
			          ResultSet rs3=stmt.executeQuery("select count(*) as total from "+table_name);
			          rs3.next();
			          int rcount1=rs3.getInt("total");
			       /***************************************************************/	
			       /***************************************************************/	
			        ResultSet rs2=stmt.executeQuery("select * from "+table_name);
			        for(int j=0;j<rcount1;j++)
			        {
			        	System.out.println("in for loop for trash j="+(j+1));
			        	 rs2.absolute(j+1);
			        	 rs2.updateString("id"+table_name,""+(j+1));
			        	 rs2.updateRow();
			        }
			        /************************************************************************/	  
			    }
			    catch(Exception del)
			    {
			    	JOptionPane.showMessageDialog(f,del.getMessage(),"error",JOptionPane.INFORMATION_MESSAGE);
			    	del.printStackTrace();
			    }
	}
	public void reStore()
	{
		try
				{
			     for(int i=0;i<table.getRowCount();i++)
			     {
			      Boolean checked = (Boolean) model.getValueAt(i,0);
			      if(checked==true)
			      {
			      	Object val1=model.getValueAt(i,1);
			      	Object val2=model.getValueAt(i,2);
			      	Object val3=model.getValueAt(i,3);
			      	System.out.println("i="+i);
			      	ResultSet rs=stmt.executeQuery("select * from trash where idtrash='"+(i+1)+"'");
			      	rs.next();
			      	String name=rs.getString("tablename");
			      	String date=rs.getString("date");
			        System.out.println("tablename="+name);
			         /***********************************************************/
			          ResultSet rs1=stmt.executeQuery("select count(*) as total from "+name);
			          rs1.next();
			          int rcount=rs1.getInt("total");
			          /***************************************************************/	
			        stmt.executeUpdate("insert into "+name+" values('"+(rcount+1)+"','"+(String)val1+"','"+(String)val2+"','"+(String)val3+"','"+date+"')");
			        stmt.executeUpdate("delete from trash where idtrash='"+(i+1)+"'");
			       
			        /************************************************************************/
			      }
			      checked=false;
			     }
			     tablemaker(table_name);
			      /***********************************************************/
			          ResultSet rs3=stmt.executeQuery("select count(*) as total from trash");
			          rs3.next();
			          int rcount1=rs3.getInt("total");
			       /***************************************************************/	
			        ResultSet rs2=stmt.executeQuery("select * from trash");
			        for(int j=0;j<rcount1;j++)
			        {
			        	System.out.println("in for loop for trash j="+(j+1));
			        	 rs2.absolute(j+1);
			        	 rs2.updateString("idtrash",""+(j+1));
			        	 rs2.updateRow();
			        }
				}
				catch(Exception res)
				{
				   JOptionPane.showMessageDialog(f,res.getMessage(),"error",JOptionPane.INFORMATION_MESSAGE);	
				   res.printStackTrace();
				}  
	}
	public static void main(String args[])
	{
		SwingUtilities.invokeLater(new Runnable()
    	{
    		public void run()
    		{
    			try
    			{
    				MailApp m=new MailApp();
    			}
    			catch(Exception e)
    			{
    				e.printStackTrace();
    			}
    		}
    		
    	});
	
	}
}