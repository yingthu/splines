package cs4620.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SliderPanel extends JPanel implements ChangeListener {
	private static final long serialVersionUID = 3059436600608867453L;

	/** The GUI components */
	private JSlider slider;
	private JTextField textField;
	private DecimalFormat df1;
	private float sliderValue;

	// Configuration of the slider
	int numTicks;
	float initialValue;
	float minValue;
	float maxValue;
	boolean log;

	public SliderPanel(ChangeListener changeListener, float minValue,
			float maxValue, float initialValue, boolean log, int numTicks, String dispFormat) {
		super();

		this.initialValue = initialValue;
		this.numTicks     = numTicks;
		this.minValue     = minValue;
		this.maxValue     = maxValue;
		this.log          = log;
		this.df1          = new DecimalFormat(dispFormat);

		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

		slider = new JSlider(JSlider.VERTICAL);
		slider.setMinorTickSpacing(10);
		slider.setMaximum(numTicks);
		slider.setMinimum(0);
		slider.setPaintTicks(true);
		slider.setValue(getTickValue(initialValue));
		slider.addChangeListener(this);
		slider.addChangeListener(changeListener);
		this.add(slider, BorderLayout.CENTER);

		textField = new JTextField();
		textField.setEditable(false);
		textField.setText(df1.format(sliderValue));
		textField.setHorizontalAlignment(JTextField.RIGHT);

		JPanel southPanel = new JPanel(new GridLayout(1, 1));
		southPanel.add(textField);
		this.add(southPanel, BorderLayout.SOUTH);

		stateChanged(null);
	}

	private float getSliderValue(JSlider source) {

		float value;
		value = source.getValue() / (float) numTicks;
		value = minValue + value * (maxValue - minValue);
		if (log)
			value = (float) Math.pow(10, value);
		return value;

	}

	private int getTickValue(float value)
	{
		if (log)
			value = (float) Math.log10(value);
		value = (value - minValue) / (maxValue - minValue);
		return (int)(value * (float) numTicks);
	}

	public void stateChanged(ChangeEvent e) {
		sliderValue = getSliderValue(slider);
		textField.setText(df1.format(sliderValue));
		repaint();
	}

	public float getValue() {
		return sliderValue;
	}

	public JSlider getSlider() {
		return slider;
	}
}