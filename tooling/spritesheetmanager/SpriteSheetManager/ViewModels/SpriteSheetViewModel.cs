using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Runtime.CompilerServices;
using System.Windows;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using SpriteSheetManager.Converters;
using SpriteSheetManager.Interfaces;

namespace SpriteSheetManager.ViewModels
{
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
}
