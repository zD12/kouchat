
/***************************************************************************
 *   Copyright 2006-2007 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

package net.usikkert.kouchat.ui.swing;

import net.usikkert.kouchat.Constants;

import java.awt.BorderLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class KouChatFrame extends JFrame
{
	private MainPanel mainP;
	private SidePanel sideP;
	private ButtonPanel buttonP;
	private Mediator mediator;
	private SysTray sysTray;
	private SettingsFrame settingsFrame;
	private MenuBar menuBar;

	public KouChatFrame()
	{
		buttonP = new ButtonPanel();
		sideP = new SidePanel( buttonP );
		mainP = new MainPanel( sideP );
		sysTray = new SysTray();
		settingsFrame = new SettingsFrame();
		menuBar = new MenuBar();
		
		ComponentHandler compHandler = new ComponentHandler();
		compHandler.setGui( this );
		compHandler.setButtonPanel( buttonP );
		compHandler.setSidePanel( sideP );
		compHandler.setMainPanel( mainP );
		compHandler.setSysTray( sysTray );
		compHandler.setSettingsFrame( settingsFrame );
		compHandler.setMenuBar( menuBar );
		
		mediator = new SwingMediator( compHandler );
		buttonP.setMediator( mediator );
		sideP.setMediator( mediator );
		mainP.setMediator( mediator );
		sysTray.setMediator( mediator );
		settingsFrame.setMediator( mediator );
		menuBar.setMediator( mediator );
		
		setJMenuBar( menuBar );
		getContentPane().add( mainP, BorderLayout.CENTER );
		setTitle( Constants.APP_NAME + " v" + Constants.APP_VERSION + " - (Not connected)" );
		setIconImage( new ImageIcon( getClass().getResource( "/icons/kou_normal.png" ) ).getImage() );
		setSize( 650, 480 );
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		setVisible( true );

		/* If this window is focused, the textfield will get keyboard events
		 * no matter which component in the window was focused when typing was started. */
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( new KeyEventDispatcher()
		{
			public boolean dispatchKeyEvent( KeyEvent e )
			{
				if( e.getID() == KeyEvent.KEY_TYPED && isFocused() )
				{
					KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent( mainP.getMsgTF(), e );
					mainP.getMsgTF().requestFocus();

					return true;
				}

				else
					return false;
			}
		} );

		// Minimize with Escape key
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false );

		Action escapeAction = new AbstractAction()
		{
			public void actionPerformed( ActionEvent e )
			{
				setVisible( false );
			}
		};

		getRootPane().getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( escapeKeyStroke, "ESCAPE" );
		getRootPane().getActionMap().put( "ESCAPE", escapeAction );

		// Shut down the right way
		addWindowListener( new WindowAdapter()
		{
			public void windowClosing( WindowEvent arg0 )
			{
				SwingUtilities.invokeLater( new Runnable()
				{
					@Override
					public void run()
					{
						mediator.quit();
					}
				} );
			}
		} );

		mediator.start();
	}
}