using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.IO;
using System.Runtime.CompilerServices;
using System.Windows;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using SpriteSheetManager.Annotations;
using SpriteSheetManager.Converters;
using SpriteSheetManager.Interfaces;

namespace SpriteSheetManager.ViewModels
{
    public abstract class ObservableObject : INotifyPropertyChanged
    {
        public event PropertyChangedEventHandler PropertyChanged;

        [NotifyPropertyChangedInvocator]
        protected virtual void RaisePropertyChanged([CallerMemberName] string propertyName = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }
    public class SpriteSheetViewModel : ObservableObject
    {
        private const string BaseDir = @"c:\projects";

        private readonly string _spriteSheetFileName = Path.Combine(BaseDir,
            @"kids.from.the.wasteland\src\android\assets\tiles\darkdirt\darkdirt.json");

        private readonly ConverterService _converterService = new ConverterService();

        private ISpriteSheet _spriteSheet;
        private BitmapImage _spriteSheetImage;
        private SpriteSheetFrameViewModel _selectedFrame;
        public ObservableCollection<SpriteSheetFrameViewModel> Frames { get; set; }
        public SpriteSheetViewModel()
        {
            //Load the one with the pixiJSConverter
            SpriteSheet = _converterService.Converters[ConverterService.PixiJjs].ReadSpriteSheet(_spriteSheetFileName);
            SpriteSheetImage = new BitmapImage();
            SpriteSheetImage.BeginInit();
            SpriteSheetImage.UriSource = new Uri(Path.Combine(SpriteSheet.BaseDir, SpriteSheet.ImageFileName));
            SpriteSheetImage.EndInit();
            SetupFrames();
        }

        private void SetupFrames()
        {
            var list = new List<SpriteSheetFrameViewModel>();
            foreach (var frame in SpriteSheet.Frames)
            {
                var croppedBitmap = new CroppedBitmap();
                croppedBitmap.BeginInit();
                croppedBitmap.Source = SpriteSheetImage;
                croppedBitmap.SourceRect = frame.TextureRegion.ToInt32Rect();
                croppedBitmap.EndInit();

                list.Add(new SpriteSheetFrameViewModel(frame, croppedBitmap));
            }
            Frames = new ObservableCollection<SpriteSheetFrameViewModel>(list);
            RaisePropertyChanged(nameof(Frames));
        }

        public BitmapImage SpriteSheetImage
        {
            get => _spriteSheetImage;
            set
            {
                _spriteSheetImage = value;
                RaisePropertyChanged(nameof(SpriteSheetImage));
            }
        }


        public ISpriteSheet SpriteSheet
        {
            get => _spriteSheet;
            set
            {
                _spriteSheet = value;
                RaisePropertyChanged(nameof(SpriteSheet));
            }
        }

        public SpriteSheetFrameViewModel SelectedFrame
        {
            get => _selectedFrame;
            set
            {
                if (_selectedFrame != null)
                    _selectedFrame.IsEditing = false;
                _selectedFrame = value;
                _selectedFrame.IsEditing = true;
                RaisePropertyChanged(nameof(SelectedFrame));
            }
        }

        private double _imageHeight;
        private double _imageWidth;
        private double _imageY;
        private double _imageX;


        protected override void RaisePropertyChanged([CallerMemberName] string propertyName = null)
        {
            switch (propertyName)
            {
                case "SelectedFrame":
                    RecalculateFrameSize();
                    break;
            }
            base.RaisePropertyChanged(propertyName);
        }

        private void RecalculateFrameSize()
        {
            var xScale = _imageWidth / SpriteSheetImage.PixelWidth;
            var yScale = xScale; //Preserve aspect ratio, maybe?
            SelectedFrame?.Rescale(xScale, yScale, _imageX, _imageY);
        }

        internal void ImageSizeChanged(double x, double y, double width, double height)
        {
            _imageX = x;
            _imageY = y;
            _imageWidth = width;
            _imageHeight = height;
            RecalculateFrameSize();
        }
    }

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
