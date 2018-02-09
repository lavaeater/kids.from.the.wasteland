using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
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
        }

        private void FrameKeyTextBox_OnIsVisibleChanged(object sender, DependencyPropertyChangedEventArgs e)
        {
            if(e.Property.Name == nameof(IsVisible) && (bool)e.NewValue)
               Keyboard.Focus(sender as IInputElement);
        }
    }
}
