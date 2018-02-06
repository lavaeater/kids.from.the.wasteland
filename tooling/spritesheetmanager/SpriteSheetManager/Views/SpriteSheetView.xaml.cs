using System.Windows;
using System.Windows.Controls;
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
    }
}
