using System.Windows.Media.Imaging;
using SpriteSheetManager.Interfaces;
using SpriteSheetManager.ViewModels;

namespace SpriteSheetManagerUI.ViewModels
{
    public class SpriteSheetFrameViewModel : ObservableObject
    {
        private double _recX;
        private double _recY;
        private double _recWidth;
        private double _recHeight;
        public ISpriteSheetFrame Frame { get; }
        public CroppedBitmap Bitmap { get; }
        public int Width => (int)Frame.SourceSize.Width * 3;
        public int Height => (int)Frame.SourceSize.Height * 3;

        private bool _isEditing;
        public bool IsEditing
        {
            get => _isEditing;
            set
            {
                _isEditing = value;
                RaisePropertyChanged(nameof(IsEditing));
            }
        }

        public double RecX
        {
            get => _recX;
            set
            {
                _recX = value;
                RaisePropertyChanged(nameof(RecX));
            }
        }

        public double RecY
        {
            get => _recY;
            set
            {
                _recY = value;
                RaisePropertyChanged(nameof(RecY));
            }
        }

        public double RecWidth
        {
            get => _recWidth;
            set
            {
                _recWidth = value;
                RaisePropertyChanged(nameof(RecWidth));
            }
        }

        public double RecHeight
        {
            get => _recHeight;
            set
            {
                _recHeight = value;
                RaisePropertyChanged(nameof(RecHeight));
            }
        }

        public SpriteSheetFrameViewModel(ISpriteSheetFrame frame, CroppedBitmap bitmap)
        {
            Frame = frame;
            Bitmap = bitmap;
            Rescale(1.0, 1.0, 0, 0);
        }

        public void Rescale(double xScale, double yScale, double startX, double startY)
        {
            RecX = startX + Frame.TextureRegion.X * xScale;
            RecY = startY +  Frame.TextureRegion.Y * yScale;
            RecWidth = Frame.TextureRegion.Width * xScale;
            RecHeight = Frame.TextureRegion.Height * yScale;
        }
    }
}