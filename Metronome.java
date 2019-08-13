import java.awt.event.*;
import java.awt.Font;
import javax.swing.*;
import javax.swing.event.*;
import java.lang.*; // for currentTimeMillis
import java.io.*;
import javax.sound.sampled.*; // for sound

public class Metronome implements ActionListener
{
	static final int WIDTH = 1600;
	static final int HEIGHT = 900;

	static final int MAXBPM = 300;

	static boolean workingFlag = false;

	static JButton bStart;
	static JButton bStop;

	static JRadioButton mode1;
	static JRadioButton mode2;
//	static JRadioButton mode3;

	static JSlider sBpm;
	static JTextField displayBpm;

	static JSlider sBpbpm;
	static JTextField displayBpbpm;

	static int valBpm;
	static int valBpbpm;

	Metronome()
	{
		JFrame f = new JFrame("Metronome"); // creating a frame
		f.setSize(WIDTH, HEIGHT); // 1600 width and 900 height
		f.setLayout(null); // using no layout managers
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel pnl = new JPanel();
		pnl.setBounds(0, 0, WIDTH, HEIGHT);
		pnl.setLayout(null);
		f.add(pnl);

		bStart = new JButton("START");
		bStart.setBounds(WIDTH*1/10, HEIGHT*8/10, WIDTH*3/10, HEIGHT*1/10);
		bStart.addActionListener(this);
		pnl.add(bStart);

		bStop = new JButton("STOP");
		bStop.setBounds(WIDTH*6/10, HEIGHT*8/10, WIDTH*3/10, HEIGHT*1/10);
		bStop.addActionListener(this);
		pnl.add(bStop);

		ButtonGroup modes = new ButtonGroup();
		mode1 = new JRadioButton("Manual", true);
		mode2 = new JRadioButton("Semi-automatic");
//		mode3 = new JRadioButton("Automatic");
		mode1.setBounds(WIDTH*1/15, HEIGHT*12/30, WIDTH*2/10, HEIGHT*1/30);
		mode2.setBounds(WIDTH*1/15, HEIGHT*13/30, WIDTH*2/10, HEIGHT*1/30);
//		mode3.setBounds(WIDTH*1/15, HEIGHT*14/30, WIDTH*2/10, HEIGHT*1/30);
		mode1.addActionListener(this);
		mode2.addActionListener(this);
//		mode3.addActionListener(this);
		modes.add(mode1); modes.add(mode2); //modes.add(mode3);
		pnl.add(mode1); pnl.add(mode2); //pnl.add(mode3);

		int valBpm = 120;
		sBpm = new JSlider(JSlider.HORIZONTAL, 30, 300, valBpm); // 30-300 bpm, initial: 120 bpm
		sBpm.setBounds(WIDTH*1/20, HEIGHT*7/10, WIDTH*9/10, HEIGHT*1/20);
		sBpm.setMinorTickSpacing(1);
		sBpm.setMajorTickSpacing(10);
		sBpm.setPaintTicks(true);
		sBpm.setPaintLabels(true);
		pnl.add(sBpm);

		displayBpm = new JTextField();
		displayBpm.setFont(new Font("Monospaced", Font.BOLD, 100));
		displayBpm.setHorizontalAlignment(JTextField.CENTER);
		displayBpm.setBounds(WIDTH*3/10, HEIGHT*1/10, WIDTH*4/10, HEIGHT*3/10);
		displayBpm.setEditable(false);
		pnl.add(displayBpm);

		int valBpbpm = 10;
		sBpbpm = new JSlider(JSlider.HORIZONTAL, 1, 50, valBpbpm); // 1-50 bpbpm, initial: 10 bpbpm
		sBpbpm.setBounds(WIDTH*1/20, HEIGHT*6/10, WIDTH*9/10, HEIGHT*1/20);
//		sBpbpm.setMinorTickSpacing(1);
		sBpbpm.setMajorTickSpacing(1);
		sBpbpm.setPaintTicks(true);
		sBpbpm.setPaintLabels(true);
		sBpbpm.setEnabled(false);
		pnl.add(sBpbpm);

		displayBpbpm = new JTextField();
		displayBpbpm.setFont(new Font("Monospaced", Font.BOLD, 60));
		displayBpbpm.setHorizontalAlignment(JTextField.CENTER);
		displayBpbpm.setBounds(WIDTH*1/20, HEIGHT*3/20, WIDTH*2/10, HEIGHT*2/10);
		displayBpbpm.setEditable(false);
		pnl.add(displayBpbpm);

		f.setVisible(true); // making the frame visible
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == bStart) workingFlag = true;
		else if (e.getSource() == bStop) workingFlag = false;
		else if (e.getSource() == mode1)
		{
			workingFlag = false;
			sBpbpm.setEnabled(false);
		}
		else if (e.getSource() == mode2)
		{
			workingFlag = false;
			displayBpbpm.setText("" + valBpbpm + " BPBPM"); // initialization
			sBpbpm.setEnabled(true);
		}
	}

	public static void beep(int bpm, long start, AudioInputStream s, Clip clip)
	{
		try
		{
			while (System.currentTimeMillis() < (start + 60000/bpm)) {} // wait for the proper moment

			if (!clip.isOpen()) clip.open(s);
			clip.stop();
			clip.setFramePosition(0);
			clip.start();
//			System.out.print("tick");
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}

	public static void main(String[] args)
	{
		new Metronome();
		while (true)
		{
			try
			{
				displayBpm.setText("" + valBpm + " BPM"); // initialization
				File snd = new File("./beep.wav");
				AudioInputStream s = AudioSystem.getAudioInputStream(snd);
				AudioFormat f = s.getFormat();
				DataLine.Info i = new DataLine.Info(Clip.class, f);
				Clip clip = (Clip) AudioSystem.getLine(i);

				if (workingFlag)
				{
					if (mode1.isSelected())
					{
						displayBpbpm.setText("");
						while (workingFlag)
						{
							long start = System.currentTimeMillis();
							valBpm = sBpm.getValue();
							displayBpm.setText("" + valBpm + " BPM");
							beep(valBpm, start, s, clip);
						}
					}
					if (mode2.isSelected())
					{
						while (workingFlag)
						{
							valBpbpm = sBpbpm.getValue();
							displayBpbpm.setText("" +valBpbpm + " BPBPM");
							for (int ii = 0; ii < valBpbpm; ii++)
							{
								long start = System.currentTimeMillis();
								valBpm = sBpm.getValue();
								displayBpm.setText("" + valBpm + " BPM");
								beep(valBpm, start, s, clip);
							}
							valBpm++;
							if (valBpm >= MAXBPM) workingFlag = false;
							sBpm.setValue(valBpm);
						}
					}
				}
				clip.close(); // close the beep.wav file
				s.close();
			}
			catch (Exception e)
			{
				System.out.println(e);
			}
		}
	}
}
