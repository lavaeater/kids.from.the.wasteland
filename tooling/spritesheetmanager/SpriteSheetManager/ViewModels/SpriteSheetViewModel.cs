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
        private const string BaseDir = @"c:\projects\private";

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
                _selectedFrame = value; 
                RaisePropertyChanged(nameof(SelectedFrame));
            }
        }

        public double CanvasHeight { get => _canvasHeight;
            set
            {
                _canvasHeight = value;
                RaisePropertyChanged(nameof(CanvasHeight));
            }
        }

        public double CanvasWidth
        {
            get => _canvasWidth;
            set
            {
                _canvasWidth = value;
                RaisePropertyChanged(nameof(CanvasWidth));
            }
        }

        private double _canvasHeight;
        private double _canvasWidth;

        protected override void RaisePropertyChanged([CallerMemberName] string propertyName = null)
        {
            switch (propertyName)
            {
                case "CanvasHeight":
                case "CanvasWidth":
                case "SelectedFrame":
                    RecalculateFrameSize();
                    break;
            }
            base.RaisePropertyChanged(propertyName);
        }

        private void RecalculateFrameSize()
        {
            var scale = CanvasWidth / SpriteSheetImage.PixelWidth;
            SelectedFrame?.Rescale(scale);
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
        public int Width => (int) Frame.SourceSize.Width * 3;
        public int Height => (int) Frame.SourceSize.Height * 3;

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
            Rescale(1.0);
        }

        public void Rescale(double scale)
        {
            RecX = Frame.TextureRegion.X * scale;
            RecY = Frame.TextureRegion.Y * scale;
            RecWidth = Frame.TextureRegion.Width * scale;
            RecHeight = Frame.TextureRegion.Height * scale;
        }
    }
}
