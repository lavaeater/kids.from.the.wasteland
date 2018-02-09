using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Threading;
using SpriteSheetManager.ViewModels;

namespace SpriteSheetManager.Views
{
    /// <summary>
    /// Interaction logic for SpriteSheetView.xaml
    /// </summary>
    public partial class SpriteSheetView : UserControl
    {
        public SpriteSheetView()
        {
            InitializeComponent();
        }

        private void Image_OnSizeChanged(object sender, SizeChangedEventArgs e)
        {
            var image = sender as Image;
            if (image?.DataContext is SpriteSheetViewModel viewModel)
            {
                var offset = VisualTreeHelper.GetOffset(image);
                var width = image.ActualWidth;
                var height = image.ActualHeight;

                viewModel.ImageSizeChanged(offset.X, offset.Y, width, height);
            }
        }

        private void FrameKeyTextBox_OnGotKeyboardFocus(object sender, KeyboardFocusChangedEventArgs e)
        {
            //OK, mixing and matching, todo is to move this into the viewModel or something or another
            (sender as TextBox)?.SelectAll();
            var who = Keyboard.FocusedElement;
        }

        private void FrameKeyTextBox_OnGotFocus(object sender, RoutedEventArgs e)
        {
            Dispatcher.BeginInvoke(DispatcherPriority.Input,
                new Action(delegate () {
                    Keyboard.Focus(sender as IInputElement); // Set Keyboard Focus
                }));
        }

        private void FrameKeyTextBox_OnLostKeyboardFocus(object sender, KeyboardFocusChangedEventArgs e)
        {
            var whatsgoingon = sender;

            Console.WriteLine(sender.ToString());
            Console.WriteLine((sender as Control)?.Name);
            Console.WriteLine(Keyboard.FocusedElement.ToString());
        }
    }
}
